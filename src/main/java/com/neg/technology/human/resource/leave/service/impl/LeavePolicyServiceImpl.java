package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.service.EmployeeService;
import com.neg.technology.human.resource.leave.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponseList;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.*;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeavePolicyServiceImpl implements LeavePolicyService {

    private final EmployeeService employeeService;

    private static final Set<LocalDate> OFFICIAL_HOLIDAYS = Set.of(
            LocalDate.of(2025, Month.JANUARY, 1),
            LocalDate.of(2025, Month.APRIL, 23),
            LocalDate.of(2025, Month.MAY, 1),
            LocalDate.of(2025, Month.MAY, 19),
            LocalDate.of(2025, Month.JULY, 15),
            LocalDate.of(2025, Month.AUGUST, 30),
            LocalDate.of(2025, Month.OCTOBER, 29)
    );

    private Mono<Employee> getEmployee(Long employeeId) {
        return employeeService.findById(employeeId)
                .switchIfEmpty(Mono.error(new RuntimeException("Employee not found")))
                .cast(Employee.class);
    }

    private LocalDate getEmploymentStartDate(Employee employee) {
        LocalDateTime dt = employee.getEmploymentStartDate();
        return dt != null ? dt.toLocalDate() : null;
    }

    private LocalDate getBirthDate(Employee employee) {
        return employee.getPerson() != null ? employee.getPerson().getBirthDate() : null;
    }

    private String getGender(Employee employee) {
        return employee.getPerson() != null ? employee.getPerson().getGender() : null;
    }

    private int calculateYearsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return Period.between(startDate, endDate).getYears();
    }

    private int calculateAge(Employee employee) {
        LocalDate birthDate = getBirthDate(employee);
        return calculateYearsBetween(birthDate, LocalDate.now());
    }

    // --- Leave Policies ---

    @Override
    public Mono<LeavePolicyResponse> getAnnualLeave(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    int yearsWorked = calculateYearsBetween(getEmploymentStartDate(employee), LocalDate.now());
                    int days;

                    if (yearsWorked < 1) days = 0;
                    else if (yearsWorked < 5) days = calculateAge(employee) >= 50 ? 20 : 14;
                    else if (yearsWorked < 15) days = 20;
                    else days = 26;

                    return LeavePolicyResponse.builder()
                            .days(days)
                            .eligible(days > 0)
                            .build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getAgeBasedLeaveBonus(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    int age = calculateAge(employee);
                    int bonus = age >= 50 ? 2 : 0;
                    return LeavePolicyResponse.builder()
                            .days(bonus)
                            .eligible(bonus > 0)
                            .build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> checkBirthdayLeave(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    LocalDate date = request.getDate();
                    boolean eligible = getBirthDate(employee) != null &&
                            getBirthDate(employee).getMonth() == date.getMonth() &&
                            getBirthDate(employee).getDayOfMonth() == date.getDayOfMonth();
                    return LeavePolicyResponse.builder()
                            .eligible(eligible)
                            .days(eligible ? 1 : 0)
                            .build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getMaternityLeaveDays(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .flatMap(employee -> {
                    if (!"female".equalsIgnoreCase(getGender(employee))) {
                        return Mono.error(new RuntimeException("Maternity leave only applies to female employees"));
                    }
                    boolean multiplePregnancy = Boolean.TRUE.equals(request.getMultiplePregnancy());
                    int days = multiplePregnancy ? 140 : 112;
                    return Mono.just(
                            LeavePolicyResponse.builder()
                                    .days(days)
                                    .eligible(true)
                                    .build()
                    );
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getPaternityLeaveDays(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {

                    if (!"male".equalsIgnoreCase(getGender(employee))) {
                        return LeavePolicyResponse.builder()
                                .days(0)
                                .eligible(false)
                                .build();
                    }

                    int maxDays = 5;
                    int requested = request.getRequestedDays() != null ? request.getRequestedDays() : maxDays;

                    int approvedDays = Math.min(requested, maxDays);

                    return LeavePolicyResponse.builder()
                            .days(approvedDays)
                            .eligible(approvedDays > 0)
                            .build();
                });
    }



    @Override
    public Mono<LeavePolicyResponse> canBorrowLeave(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    Integer requestedDays = request.getRequestedDays();
                    Integer currentBorrowed = request.getCurrentBorrowed();

                    boolean allowed = false;
                    if (requestedDays != null && currentBorrowed != null) {
                        LocalDate startDate = getEmploymentStartDate(employee);
                        int monthsWorked = Period.between(startDate, LocalDate.now()).getYears() * 12 +
                                Period.between(startDate, LocalDate.now()).getMonths();

                        allowed = monthsWorked >= 3 &&
                                requestedDays <= 5 &&
                                currentBorrowed + requestedDays <= 10;
                    }

                    return LeavePolicyResponse.builder()
                            .eligible(allowed)
                            .days(allowed ? request.getRequestedDays() : 0)
                            .build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getBereavementLeave(LeavePolicyRequest request) {
        return Mono.fromCallable(() -> {
            int days = switch (request.getRelationType() != null ? request.getRelationType().toLowerCase() : "") {
                case "parent", "sibling", "child", "spouse" -> 3;
                case "grandparent", "aunt", "uncle", "in-law" -> 1;
                default -> 0;
            };
            return LeavePolicyResponse.builder()
                    .days(days)
                    .eligible(days > 0)
                    .build();
        });
    }

    @Override
    public Mono<LeavePolicyResponse> getMarriageLeave(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .flatMap(employee -> {
                    boolean firstMarriage = Boolean.TRUE.equals(request.getFirstMarriage());
                    boolean hasMarriageCertificate = Boolean.TRUE.equals(request.getIsSpouseWorking());
                    int days = (firstMarriage && hasMarriageCertificate) ? 3 : 0;
                    return Mono.just(
                            LeavePolicyResponse.builder()
                                    .days(days)
                                    .eligible(days > 0)
                                    .build()
                    );
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getMilitaryLeaveInfo(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> LeavePolicyResponse.builder()
                        .eligible(true)
                        .days(null)
                        .build());
    }

    @Override
    public Mono<LeavePolicyResponse> isHoliday(LeavePolicyRequest request) {
        return Mono.fromCallable(() -> {
            LocalDate date = request.getDate();
            boolean holiday = OFFICIAL_HOLIDAYS.contains(date) ||
                    date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    date.getDayOfWeek() == DayOfWeek.SUNDAY;
            return LeavePolicyResponse.builder()
                    .eligible(holiday)
                    .days(holiday ? 1 : 0)
                    .build();
        });
    }

    @Override
    public Mono<LeavePolicyResponseList> getAllLeavePolicies() {
        return Mono.fromCallable(() -> {
            LeavePolicyResponseList list = new LeavePolicyResponseList();
            list.setLeavePolicies(List.of(
                    LeavePolicyResponse.builder().days(14).eligible(true).build(),
                    LeavePolicyResponse.builder().days(5).eligible(true).build(),
                    LeavePolicyResponse.builder().days(112).eligible(true).build()
            ));
            return list;
        });
    }
}
