package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.exception.InvalidLeaveRequestException;
import com.neg.technology.human.resource.exception.LeaveBalanceExceededException;
import com.neg.technology.human.resource.leave.model.entity.LeaveRequest;
import com.neg.technology.human.resource.leave.model.enums.LeaveStatus;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.leave.repository.LeaveRequestRepository;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.person.model.enums.Gender;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LeaveRequestValidator {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceService leaveBalanceService;

    private static final Set<LocalDate> OFFICIAL_HOLIDAYS = Set.of(
            LocalDate.of(2025, Month.JANUARY, 1),
            LocalDate.of(2025, Month.APRIL, 23),
            LocalDate.of(2025, Month.MAY, 1),
            LocalDate.of(2025, Month.MAY, 19),
            LocalDate.of(2025, Month.JULY, 15),
            LocalDate.of(2025, Month.AUGUST, 30),
            LocalDate.of(2025, Month.OCTOBER, 29)
    );

    public Mono<Boolean> isHoliday(LocalDate date) {
        if (date == null)
            return Mono.error(InvalidLeaveRequestException.invalidRequest("Tarih boş olamaz."));
        boolean isHoliday = OFFICIAL_HOLIDAYS.contains(date) ||
                date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY;
        return Mono.just(isHoliday);
    }

    public Mono<Void> validateEligibility(Employee employee, LeaveType leaveType) {
        if (employee == null || leaveType == null)
            return Mono.error(InvalidLeaveRequestException.invalidRequest("Çalışan veya izin türü boş olamaz."));

        Gender requiredGender = leaveType.getGenderRequired();
        Gender employeeGender = employee.getPerson().getGender();

        if (requiredGender != null && requiredGender != Gender.OTHER) {
            if (requiredGender != employeeGender) {
                return Mono.error(InvalidLeaveRequestException.invalidRequest(
                        "Bu izin türü çalışanın cinsiyeti için uygun değil."));
            }
        }
        return Mono.empty();
    }

    public Mono<Boolean> hasOverlappingRequests(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingRequests(employeeId,
                startDate, endDate);
        return Mono.just(overlappingRequests.isEmpty());
    }

    public Mono<Void> validateLeaveRequestCreation(
            Employee employee,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal requestedDays) {

        boolean isAnnualLeave = "Yıllık İzin".equalsIgnoreCase(leaveType.getName());

        return hasOverlappingRequests(employee.getId(), startDate, endDate)
                .flatMap(isNotOverlapping -> {
                    if (!isNotOverlapping) {
                        return Mono.error(InvalidLeaveRequestException.invalidRequest(
                                "Bu tarih aralığında zaten mevcut bir izin talebi var."));
                    }

                    return validateEligibility(employee, leaveType)
                            .then(Mono.defer(() -> leaveBalanceService
                                    .getByEmployeeAndLeaveType(
                                            new EmployeeLeaveTypeRequest(employee.getId(), leaveType.getId()))
                                    .switchIfEmpty(Mono.error(InvalidLeaveRequestException.invalidRequest(
                                            "Bu izin türü için tanımlı bir bakiye bulunamadı.")))
                                    .flatMap(balance -> {
                                        BigDecimal available = balance.getAvailableBalance();

                                        if (isAnnualLeave) {
                                            BigDecimal maxNegative = BigDecimal.valueOf(5); // -5 gün negatif izin
                                            BigDecimal maxAllowed = available.add(maxNegative);

                                            if (requestedDays.compareTo(maxAllowed) > 0) {
                                                return Mono.error(LeaveBalanceExceededException.custom(
                                                        "Yetersiz izin bakiyesi. Maksimum -5 gün eksiye düşülebilir. "
                                                                + "Mevcut: " + available
                                                                + ", Talep edilen: " + requestedDays));
                                            }
                                        } else {
                                            if (requestedDays.compareTo(available) > 0) {
                                                return Mono.error(LeaveBalanceExceededException.custom(
                                                        "Yetersiz izin bakiyesi. Mevcut: " + available
                                                                + ", Talep edilen: " + requestedDays));
                                            }
                                        }

                                        return Mono.empty();
                                    })));
                });
    }

    public Mono<Void> validateStatusChange(LeaveRequest existingRequest, LeaveStatus newStatus) {
        LeaveStatus oldStatus = existingRequest.getStatus();

        if (newStatus == LeaveStatus.APPROVED &&
                (oldStatus == LeaveStatus.REJECTED || oldStatus == LeaveStatus.CANCELLED)) {
            return Mono.error(InvalidLeaveRequestException.invalidRequest(
                    "Reddedilmiş veya iptal edilmiş izin talebi onaylanamaz."));
        }

        return Mono.empty();
    }

    public Mono<Void> validateLeaveRequestUpdate(
            LeaveRequest existing,
            Employee employee,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal requestedDays) {

        return hasOverlappingRequests(employee.getId(), startDate, endDate)
                .flatMap(isNotOverlapping -> {
                    List<LeaveRequest> overlapping = leaveRequestRepository
                            .findOverlappingRequests(employee.getId(), startDate, endDate)
                            .stream()
                            .filter(req -> !req.getId().equals(existing.getId()))
                            .toList();

                    if (!overlapping.isEmpty()) {
                        return Mono.error(InvalidLeaveRequestException.invalidRequest(
                                "Bu tarih aralığında zaten mevcut bir izin talebi var."));
                    }

                    // Creation validasyonunu çağır
                    return validateLeaveRequestCreation(employee, leaveType, startDate, endDate, requestedDays);
                });
    }
}
