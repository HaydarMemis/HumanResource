package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.employee.model.entity.Employee;
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
                        LocalDate.of(2025, Month.OCTOBER, 29));

        public Mono<Boolean> isHoliday(LocalDate date) {
                if (date == null)
                        return Mono.error(new IllegalArgumentException("Date cannot be null"));
                boolean isHoliday = OFFICIAL_HOLIDAYS.contains(date) ||
                                date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                date.getDayOfWeek() == DayOfWeek.SUNDAY;
                return Mono.just(isHoliday);
        }

        public Mono<Void> validateEligibility(Employee employee, LeaveType leaveType) {
                if (employee == null || leaveType == null)
                        return Mono.error(new IllegalArgumentException("Employee or leave type cannot be null"));

                Gender requiredGender = leaveType.getGenderRequired();
                Gender employeeGender = employee.getPerson().getGender();

                if (requiredGender != null && requiredGender != Gender.OTHER) {
                        if (requiredGender != employeeGender) {
                                return Mono.error(new IllegalArgumentException(
                                                "This leave type is not for the employee's gender."));
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
                                                return Mono.error(new RuntimeException(
                                                                "An existing leave request already covers this date range."));
                                        }

                                        return validateEligibility(employee, leaveType)
                                                        .then(Mono.defer(() -> leaveBalanceService
                                                                        .getByEmployeeAndLeaveType(
                                                                                        new EmployeeLeaveTypeRequest(
                                                                                                        employee.getId(),
                                                                                                        leaveType.getId()))
                                                                        .switchIfEmpty(Mono.error(new RuntimeException(
                                                                                        "No leave balance defined for this leave type.")))
                                                                        .flatMap(balance -> {
                                                                                BigDecimal available = balance
                                                                                                .getAvailableBalance();

                                                                                if (isAnnualLeave) {
                                                                                        BigDecimal maxNegative = BigDecimal
                                                                                                        .valueOf(5); // -5
                                                                                                                     // gün
                                                                                                                     // negatif
                                                                                                                     // izin
                                                                                        BigDecimal maxAllowed = available
                                                                                                        .add(maxNegative);

                                                                                        if (requestedDays.compareTo(
                                                                                                        maxAllowed) > 0) {
                                                                                                return Mono.error(
                                                                                                                new LeaveBalanceExceededException(
                                                                                                                                "Yetersiz izin bakiyesi. Maximum negative allowed: -5 gün. "
                                                                                                                                                +
                                                                                                                                                "Available: "
                                                                                                                                                + available
                                                                                                                                                +
                                                                                                                                                ", Requested: "
                                                                                                                                                + requestedDays));
                                                                                        }
                                                                                } else {
                                                                                        if (requestedDays.compareTo(
                                                                                                        available) > 0) {
                                                                                                return Mono.error(
                                                                                                                new LeaveBalanceExceededException(
                                                                                                                                "Insufficient leave balance. Available: "
                                                                                                                                                + available
                                                                                                                                                +
                                                                                                                                                ", Requested: "
                                                                                                                                                + requestedDays));
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
                        return Mono.error(new IllegalArgumentException(
                                        "Cannot approve a rejected or cancelled leave request."));
                }

                return Mono.empty();
        }

}