package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.service.EmployeeService;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        if (employeeId == null) return Mono.error(new IllegalArgumentException("Çalışan ID'si boş olamaz"));
        return employeeService.findById(employeeId)
                .switchIfEmpty(Mono.error(new RuntimeException("Çalışan bulunamadı, id: " + employeeId)))
                .cast(Employee.class);
    }

    private LocalDate getEmploymentStartDate(Employee employee) {
        if (employee == null) return null;
        LocalDateTime dt = employee.getEmploymentStartDate();
        return dt != null ? dt.toLocalDate() : null;
    }

    private LocalDate getBirthDate(Employee employee) {
        if (employee != null && employee.getPerson() != null) {
            return employee.getPerson().getBirthDate();
        }
        return null;
    }

    private String getGender(Employee employee) {
        return employee != null && employee.getPerson() != null ? employee.getPerson().getGender() : null;
    }

    private int calculateYearsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return Period.between(startDate, endDate).getYears();
    }

    private int calculateAge(Employee employee) {
        LocalDate birthDate = getBirthDate(employee);
        if (birthDate == null) {
            throw new IllegalArgumentException("Çalışanın doğum tarihi bilgisi eksik veya erişilemiyor.");
        }
        return calculateYearsBetween(birthDate, LocalDate.now());
    }

    // --- Genel max gün kontrolü ---
    @Override
    public Mono<Integer> getMaxAllowedDaysForEmployeeAndType(LeavePolicyRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("employeeId ve leaveTypeId zorunludur"));
        }

        return getEmployee(request.getEmployeeId())
                .flatMap(employee -> {
                    LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                            .orElseThrow(() -> new RuntimeException("LeaveType bulunamadı: " + request.getLeaveTypeId()));

                    String name = leaveType.getName() != null ? leaveType.getName().trim().toLowerCase() : "";

                    // Doğum izni
                    if ("maternity leave".equalsIgnoreCase(name)) {
                        String gender = getGender(employee);
                        if (!"female".equalsIgnoreCase(gender)) {
                            return Mono.error(new RuntimeException("Doğum izni sadece kadın çalışanlar içindir"));
                        }
                        boolean multiplePregnancy = Boolean.TRUE.equals(request.getMultiplePregnancy());
                        int max = multiplePregnancy ? 140 : 112;
                        return Mono.just(max);
                    }

                    // Babalık izni
                    if ("paternity leave".equalsIgnoreCase(name)) {
                        String gender = getGender(employee);
                        if (!"male".equalsIgnoreCase(gender)) {
                            return Mono.error(new RuntimeException("Babalık izni sadece erkek çalışanlar içindir"));
                        }
                        Integer max = leaveType.getMaxDays() != null ? leaveType.getMaxDays() : 5;
                        return Mono.just(max);
                    }

                    // Yıllık izin
                    if ("annual leave".equalsIgnoreCase(name)) {
                        int yearsWorked = 0;
                        if (employee.getEmploymentStartDate() != null) {
                            yearsWorked = (int) employee.getEmploymentStartDate()
                                    .until(java.time.LocalDateTime.now(), java.time.temporal.ChronoUnit.YEARS);
                        }
                        int max;
                        if (yearsWorked < 1) max = 0;
                        else if (yearsWorked < 5) max = 14;
                        else if (yearsWorked < 15) max = 20;
                        else max = 26;
                        return Mono.just(max);
                    }

                    // Diğer izin tipleri
                    if (leaveType.getMaxDays() != null) {
                        return Mono.just(leaveType.getMaxDays());
                    }

                    // Varsayılan: sınırsız izin
                    return Mono.just(Integer.MAX_VALUE);
                });
    }


    // --- Leave kuralları ---
    @Override
    public Mono<LeavePolicyResponse> getAnnualLeave(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    LocalDate startDate = getEmploymentStartDate(employee);
                    if (startDate == null) return LeavePolicyResponse.builder().days(0).eligible(false).build();
                    int yearsWorked = calculateYearsBetween(startDate, LocalDate.now());
                    int days;
                    try {
                        days = yearsWorked < 1 ? 0 :
                                yearsWorked < 5 ? (calculateAge(employee) >= 50 ? 20 : 14) :
                                        yearsWorked < 15 ? 20 : 26;
                    } catch (IllegalArgumentException e) {
                        // Doğum tarihi yoksa yaş bazlı ek gün verilmez, sadece kıdeme bakılır
                        days = yearsWorked < 1 ? 0 :
                                yearsWorked < 5 ? 14 :
                                        yearsWorked < 15 ? 20 : 26;
                    }
                    return LeavePolicyResponse.builder().days(days).eligible(days > 0).build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getAgeBasedLeaveBonus(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    int bonus = 0;
                    try {
                        bonus = calculateAge(employee) >= 50 ? 2 : 0;
                    } catch (IllegalArgumentException e) {
                        // Doğum tarihi yoksa yaş bazlı bonus verilmez
                        bonus = 0;
                    }
                    return LeavePolicyResponse.builder().days(bonus).eligible(bonus > 0).build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> checkBirthdayLeave(LeavePolicyRequest request) {
        if (request.getDate() == null) return Mono.error(new IllegalArgumentException("Tarih boş olamaz"));
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
            return Mono.error(new IllegalArgumentException("RequestedDays ve CurrentBorrowed boş olamaz"));
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
        if (request.getDate() == null) return Mono.error(new IllegalArgumentException("Tarih boş olamaz"));
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

    @Override
    public int getLeaveDaysByPolicy(Employee employee, LeaveType leaveType, int year) {
        if (employee == null || leaveType == null) return 0;

        String typeName = Optional.ofNullable(leaveType.getName()).orElse("").trim().toLowerCase();

        switch (typeName) {
            case "annual leave": {
                LocalDate startDate = getEmploymentStartDate(employee);
                if (startDate == null) return 0;
                LocalDate referenceDate = LocalDate.of(year, 12, 31);
                int yearsWorked = calculateYearsBetween(startDate, referenceDate);
                int yas = 0;
                try {
                    yas = calculateAge(employee);
                } catch (IllegalArgumentException e) {
                    yas = 0;
                }
                if (yearsWorked < 1) return 0;
                if (yearsWorked < 5) return yas >= 50 ? 20 : 14;
                if (yearsWorked < 15) return 20;
                return 26;
            }
            case "maternity leave": {
                if (!"female".equalsIgnoreCase(getGender(employee))) return 0;
                return leaveType.getMaxDays() != null ? leaveType.getMaxDays() : 112;
            }
            case "paternity leave": {
                if (!"male".equalsIgnoreCase(getGender(employee))) return 0;
                return leaveType.getMaxDays() != null ? leaveType.getMaxDays() : 5;
            }
            default: {
                return leaveType.getMaxDays() != null ? leaveType.getMaxDays() : 0;
            }
        }
    }
}
