package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.leave.model.entity.LeaveRequest;
import com.neg.technology.human.resource.leave.model.enums.Gender;
import com.neg.technology.human.resource.leave.model.enums.LeaveStatus;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.request.ChangeLeaveRequestStatusRequest;
import com.neg.technology.human.resource.leave.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.leave.repository.LeaveRequestRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
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

    /**
     * Checks if a given date is a holiday (official holiday or weekend).
     */
    public Mono<Boolean> isHoliday(LocalDate date) {
        if (date == null) {
            return Mono.error(new IllegalArgumentException("Date cannot be null"));
        }
        boolean isHoliday = OFFICIAL_HOLIDAYS.contains(date) ||
                date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY;
        return Mono.just(isHoliday);
    }

    /**
     * Checks if an employee is eligible for a specific leave type based on criteria like gender.
     */
    public Mono<Void> validateEligibility(Employee employee, LeaveType leaveType) {
        if (employee == null || leaveType == null) {
            return Mono.error(new IllegalArgumentException("Employee or leave type cannot be null"));
        }

        Gender requiredGender = leaveType.getGenderRequired();
        Gender employeeGender = employee.getPerson().getGender();

        if (requiredGender != null && requiredGender != Gender.NONE) {
            if (requiredGender != employeeGender) {
                return Mono.error(new IllegalArgumentException(
                        "This leave type is not for the employee's gender."
                ));
            }
        }
        return Mono.empty();
    }

    /**
     * Checks if an employee has any overlapping leave requests for the specified dates.
     */
    public Mono<Boolean> hasOverlappingRequests(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingRequests(employeeId, startDate, endDate);
        return Mono.just(overlappingRequests.isEmpty());
    }

    /**
     * Validates a new leave request before creation.
     */
    public Mono<Void> validateLeaveRequestCreation(Employee employee, LeaveType leaveType, LocalDate startDate, LocalDate endDate, BigDecimal requestedDays) {
        return hasOverlappingRequests(employee.getId(), startDate, endDate)
                .flatMap(isNotOverlapping -> {
                    if (Boolean.FALSE.equals(isNotOverlapping)) {
                        return Mono.error(new RuntimeException("An existing leave request already covers this date range."));
                    }
                    return validateEligibility(employee, leaveType);
                })
                .then(Mono.fromCallable(() -> {
                    return leaveBalanceService.getByEmployeeAndLeaveType(new EmployeeLeaveTypeRequest(employee.getId(), leaveType.getId()))
                            .doOnNext(balance -> {
                                if (balance.getTotalAmount().compareTo(requestedDays) < 0) {
                                    throw new RuntimeException("Insufficient leave balance.");
                                }
                            })
                            .then();
                })).then();
    }


    public Mono<Void> validateStatusChange(LeaveRequest existingRequest, ChangeLeaveRequestStatusRequest dto) {
        LeaveStatus oldStatus = existingRequest.getStatus();
        LeaveStatus newStatus = dto.getStatus(); // Artık doğrudan enum geliyor

        if (newStatus == LeaveStatus.APPROVED && (oldStatus == LeaveStatus.REJECTED || oldStatus == LeaveStatus.CANCELLED)) {
            return Mono.error(new IllegalArgumentException("Cannot approve a rejected or cancelled leave request."));
        }

        // İsteğe bağlı olarak başka kurallar ekleyebilirsin
        return Mono.empty();
    }

}