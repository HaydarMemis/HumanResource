package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.service.EmployeeService;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeavePolicyServiceImpl implements LeavePolicyService {

    private final EmployeeService employeeService;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;

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
        if (employeeId == null) return Mono.error(new IllegalArgumentException("EmployeeId cannot be null"));
        return employeeService.findById(employeeId)
                .switchIfEmpty(Mono.error(new RuntimeException("Employee not found with id: " + employeeId)))
                .cast(Employee.class);
    }

    private LocalDate getEmploymentStartDate(Employee employee) {
        if (employee == null) return null;
        if (employee.getEmploymentStartDate() == null) return null;
        return employee.getEmploymentStartDate().toLocalDate();
    }

    private LocalDate getBirthDate(Employee employee) {
        return employee != null && employee.getPerson() != null ? employee.getPerson().getBirthDate() : null;
    }

    private int calculateYearsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return Period.between(startDate, endDate).getYears();
    }

    private int calculateAge(Employee employee) {
        LocalDate birthDate = getBirthDate(employee);
        return calculateYearsBetween(birthDate, LocalDate.now());
    }

    /**
     * Returns maximum allowed days for employee & leaveType based on rules.
     * - Annual leave rules: 0 if <1 year, 14 if 1-4, 20 if 5-14, 26 if >=15
     * - Age >=50 => +2 bonus days
     * - Maternity/Paternity handled separately
     */
    @Override
    public Mono<Integer> getMaxAllowedDaysForEmployeeAndType(LeavePolicyRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("employeeId and leaveTypeId are required"));
        }

        return getEmployee(request.getEmployeeId())
                .flatMap(employee -> {
                    LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                            .orElseThrow(() -> new RuntimeException("LeaveType not found: " + request.getLeaveTypeId()));

                    String name = Optional.ofNullable(leaveType.getName()).orElse("").trim().toLowerCase();

                    // Maternity Leave (ebeveynlik özel kuralları)
                    if ("maternity leave".equalsIgnoreCase(name) || "ebeveyn izni".equalsIgnoreCase(name) || "doğum izni".equalsIgnoreCase(name)) {
                        if (!"female".equalsIgnoreCase(employee.getPerson() != null ? employee.getPerson().getGender() : null)) {
                            return Mono.error(new RuntimeException("Maternity leave only for female employees"));
                        }
                        boolean multiplePregnancy = Boolean.TRUE.equals(request.getMultiplePregnancy());
                        int max = multiplePregnancy ? 140 : 112; // 16 hafta * 7 = 112 gibi örnek
                        return Mono.just(max);
                    }

                    // Paternity Leave
                    if ("paternity leave".equalsIgnoreCase(name) || "babalık izni".equalsIgnoreCase(name)) {
                        if (!"male".equalsIgnoreCase(employee.getPerson() != null ? employee.getPerson().getGender() : null)) {
                            return Mono.error(new RuntimeException("Paternity leave only for male employees"));
                        }
                        Integer maxDays = leaveType.getMaxDays() != null ? leaveType.getMaxDays() : 5;
                        return Mono.just(maxDays);
                    }

                    // Annual Leave / Yıllık İzin
                    if ("annual leave".equalsIgnoreCase(name) || "yıllık izin".equalsIgnoreCase(name)) {
                        int yearsWorked = 0;
                        if (employee.getEmploymentStartDate() != null) {
                            yearsWorked = (int) employee.getEmploymentStartDate()
                                    .until(LocalDateTime.now(), ChronoUnit.YEARS);
                        }

                        int max;
                        if (yearsWorked < 1) max = 0;
                        else if (yearsWorked < 5) max = 14;
                        else if (yearsWorked < 15) max = 20;
                        else max = 26;

                        // yaş >= 50 bonus kuralı
                        if (calculateAge(employee) >= 50) {
                            max += 2;
                        }

                        return Mono.just(max);
                    }

                    // Diğer izin tipleri için leaveType içindeki maxDays alanını kullan
                    if (leaveType.getMaxDays() != null) {
                        return Mono.just(leaveType.getMaxDays());
                    }

                    // Default: sınırsız göster
                    return Mono.just(Integer.MAX_VALUE);
                });
    }

    // --- Leave kuralları (diğer yardımcı endpointler) ---
    @Override
    public Mono<LeavePolicyResponse> getAnnualLeave(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    LocalDate startDate = getEmploymentStartDate(employee);
                    if (startDate == null) return LeavePolicyResponse.builder().days(0).eligible(false).build();
                    int yearsWorked = calculateYearsBetween(startDate, LocalDate.now());
                    int days = yearsWorked < 1 ? 0 :
                            yearsWorked < 5 ? (calculateAge(employee) >= 50 ? 20 : 14) :
                                    yearsWorked < 15 ? 20 : 26;
                    return LeavePolicyResponse.builder().days(days).eligible(days > 0).build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getAgeBasedLeaveBonus(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    int bonus = calculateAge(employee) >= 50 ? 2 : 0;
                    return LeavePolicyResponse.builder().days(bonus).eligible(bonus > 0).build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> checkBirthdayLeave(LeavePolicyRequest request) {
        if (request.getDate() == null) return Mono.error(new IllegalArgumentException("Date cannot be null"));
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    LocalDate birthDate = getBirthDate(employee);
                    boolean eligible = birthDate != null &&
                            birthDate.getMonth() == request.getDate().getMonth() &&
                            birthDate.getDayOfMonth() == request.getDate().getDayOfMonth();
                    return LeavePolicyResponse.builder().eligible(eligible).days(eligible ? 1 : 0).build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getMaternityLeaveDays(LeavePolicyRequest request) {
        return getMaxAllowedDaysForEmployeeAndType(request)
                .map(days -> LeavePolicyResponse.builder().days(days).eligible(true).build());
    }

    @Override
    public Mono<LeavePolicyResponse> getPaternityLeaveDays(LeavePolicyRequest request) {
        return getMaxAllowedDaysForEmployeeAndType(request)
                .map(days -> LeavePolicyResponse.builder().days(days).eligible(true).build());
    }

    @Override
    public Mono<LeavePolicyResponse> canBorrowLeave(LeavePolicyRequest request) {
        if (request.getRequestedDays() == null || request.getCurrentBorrowed() == null) {
            return Mono.error(new IllegalArgumentException("RequestedDays and CurrentBorrowed cannot be null"));
        }
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    LocalDate startDate = getEmploymentStartDate(employee);
                    if (startDate == null) return LeavePolicyResponse.builder().eligible(false).days(0).build();
                    int monthsWorked = Period.between(startDate, LocalDate.now()).getYears() * 12 +
                            Period.between(startDate, LocalDate.now()).getMonths();
                    boolean allowed = monthsWorked >= 3 &&
                            request.getRequestedDays() <= 5 &&
                            request.getCurrentBorrowed() + request.getRequestedDays() <= 10;
                    return LeavePolicyResponse.builder().eligible(allowed).days(allowed ? request.getRequestedDays() : 0).build();
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
            return LeavePolicyResponse.builder().days(days).eligible(days > 0).build();
        });
    }

    @Override
    public Mono<LeavePolicyResponse> getMarriageLeave(LeavePolicyRequest request) {
        boolean firstMarriage = Boolean.TRUE.equals(request.getFirstMarriage());
        boolean hasMarriageCertificate = Boolean.TRUE.equals(request.getIsSpouseWorking());
        int days = (firstMarriage && hasMarriageCertificate) ? 3 : 0;
        return Mono.just(LeavePolicyResponse.builder().days(days).eligible(days > 0).build());
    }

    @Override
    public Mono<LeavePolicyResponse> getMilitaryLeaveInfo(LeavePolicyRequest request) {
        return Mono.just(LeavePolicyResponse.builder().eligible(true).days(null).build());
    }

    @Override
    public Mono<LeavePolicyResponse> isHoliday(LeavePolicyRequest request) {
        if (request.getDate() == null) return Mono.error(new IllegalArgumentException("Date cannot be null"));
        boolean holiday = OFFICIAL_HOLIDAYS.contains(request.getDate()) ||
                request.getDate().getDayOfWeek() == DayOfWeek.SATURDAY ||
                request.getDate().getDayOfWeek() == DayOfWeek.SUNDAY;
        return Mono.just(LeavePolicyResponse.builder().eligible(holiday).days(holiday ? 1 : 0).build());
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
