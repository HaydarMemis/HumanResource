package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.service.EmployeeService;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import com.neg.technology.human.resource.person.model.enums.Gender;
import com.neg.technology.human.resource.exception.InvalidLeaveRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LeavePolicyServiceImpl implements LeavePolicyService {

    private final EmployeeService employeeService;
    private final LeaveTypeRepository leaveTypeRepository;

    private static final Set<LocalDate> OFFICIAL_HOLIDAYS = Set.of(
            LocalDate.of(2025, Month.JANUARY, 1),
            LocalDate.of(2025, Month.APRIL, 23),
            LocalDate.of(2025, Month.MAY, 1),
            LocalDate.of(2025, Month.MAY, 19),
            LocalDate.of(2025, Month.JULY, 15),
            LocalDate.of(2025, Month.AUGUST, 30),
            LocalDate.of(2025, Month.OCTOBER, 29));

    private Mono<Employee> getEmployee(Long employeeId) {
        if (employeeId == null)
            return Mono.error(new IllegalArgumentException("EmployeeId cannot be null"));
        return employeeService.findById(employeeId)
                .switchIfEmpty(Mono.error(new RuntimeException("Employee not found with id: " + employeeId)))
                .cast(Employee.class);
    }

    private LocalDate getEmploymentStartDate(Employee employee) {
        if (employee == null)
            return null;
        LocalDateTime dt = employee.getEmploymentStartDate();
        return dt != null ? dt.toLocalDate() : null;
    }

    private LocalDate getBirthDate(Employee employee) {
        return employee != null && employee.getPerson() != null ? employee.getPerson().getBirthDate() : null;
    }

    private int calculateYearsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null)
            return 0;
        return Period.between(startDate, endDate).getYears();
    }

    private int calculateMonthsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null)
            return 0;
        Period p = Period.between(startDate, endDate);
        return p.getYears() * 12 + p.getMonths();
    }

    private int calculateAge(Employee employee) {
        LocalDate birthDate = getBirthDate(employee);
        return calculateYearsBetween(birthDate, LocalDate.now());
    }

    @Override
    public Mono<BigDecimal> getMaxAllowedDaysForEmployeeAndType(LeavePolicyRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("employeeId and leaveTypeId are required"));
        }

        return getEmployee(request.getEmployeeId())
                .flatMap(employee -> {
                    LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                            .orElseThrow(
                                    () -> new RuntimeException("LeaveType not found: " + request.getLeaveTypeId()));

                    Gender requiredGender = leaveType.getGenderRequired();
                    Gender employeeGender = employee.getPerson().getGender();

                    if (requiredGender != null && requiredGender != Gender.OTHER) {
                        if (requiredGender != employeeGender) {
                            throw InvalidLeaveRequestException.invalidRequest(
                                    "Bu izin türü (" + leaveType.getName() +
                                            ") çalışan cinsiyeti (" + employeeGender +
                                            ") için uygun değildir.");
                        }
                    }

                    if (Boolean.TRUE.equals(leaveType.getIsAnnual())) {
                        int yearsWorked = calculateYearsBetween(getEmploymentStartDate(employee), LocalDate.now());
                        BigDecimal max;
                        if (yearsWorked < 1)
                            max = BigDecimal.ZERO;
                        else if (yearsWorked < 5)
                            max = BigDecimal.valueOf(14);
                        else if (yearsWorked < 15)
                            max = BigDecimal.valueOf(20);
                        else
                            max = BigDecimal.valueOf(26);
                        return Mono.just(max);
                    }

                    if (leaveType.getMaxDays() != null) {
                        return Mono.just(BigDecimal.valueOf(leaveType.getMaxDays()));
                    }

                    return Mono.just(leaveType.getDefaultDays() != null ? BigDecimal.valueOf(leaveType.getDefaultDays())
                            : BigDecimal.ZERO);
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getAnnualLeave(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    LocalDate startDate = getEmploymentStartDate(employee);
                    if (startDate == null)
                        return LeavePolicyResponse.builder()
                                .days(BigDecimal.ZERO)
                                .eligible(false)
                                .build();

                    int yearsWorked = calculateYearsBetween(startDate, LocalDate.now());
                    BigDecimal days = BigDecimal.valueOf(
                            yearsWorked < 1 ? 0
                                    : yearsWorked < 5 ? (calculateAge(employee) >= 50 ? 20 : 14)
                                            : yearsWorked < 15 ? 20 : 26);

                    return LeavePolicyResponse.builder()
                            .days(days)
                            .eligible(days.compareTo(BigDecimal.ZERO) > 0)
                            .build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getAdvanceLeave(LeavePolicyRequest request) {
        if (request.getRequestedDays() == null || request.getCurrentBorrowed() == null) {
            return Mono.error(new IllegalArgumentException("RequestedDays and CurrentBorrowed cannot be null"));
        }
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    LocalDate startDate = getEmploymentStartDate(employee);
                    if (startDate == null)
                        return LeavePolicyResponse.builder().eligible(false).days(BigDecimal.ZERO).build();

                    int monthsWorked = calculateMonthsBetween(startDate, LocalDate.now());

                    boolean allowed = monthsWorked >= 3 &&
                            request.getRequestedDays().compareTo(BigDecimal.valueOf(5)) <= 0 &&
                            request.getCurrentBorrowed().add(request.getRequestedDays())
                                    .compareTo(BigDecimal.valueOf(10)) <= 0;

                    return LeavePolicyResponse.builder()
                            .eligible(allowed)
                            .days(allowed ? request.getRequestedDays() : BigDecimal.ZERO)
                            .build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> getAgeBasedLeaveBonus(LeavePolicyRequest request) {
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    BigDecimal bonus = calculateAge(employee) >= 50 ? BigDecimal.valueOf(2) : BigDecimal.ZERO;
                    return LeavePolicyResponse.builder().days(bonus).eligible(bonus.compareTo(BigDecimal.ZERO) > 0)
                            .build();
                });
    }

    @Override
    public Mono<LeavePolicyResponse> checkBirthdayLeave(LeavePolicyRequest request) {
        if (request.getDate() == null)
            return Mono.error(new IllegalArgumentException("Date cannot be null"));
        return getEmployee(request.getEmployeeId())
                .map(employee -> {
                    LocalDate birthDate = getBirthDate(employee);
                    boolean eligible = birthDate != null &&
                            birthDate.getMonth() == request.getDate().getMonth() &&
                            birthDate.getDayOfMonth() == request.getDate().getDayOfMonth();
                    return LeavePolicyResponse.builder()
                            .eligible(eligible)
                            .days(eligible ? BigDecimal.ONE : BigDecimal.ZERO)
                            .build();
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
    public Mono<LeavePolicyResponse> getBereavementLeave(LeavePolicyRequest request) {
        return Mono.fromCallable(() -> {
            BigDecimal days = switch (request.getRelationType() != null ? request.getRelationType().toLowerCase()
                    : "") {
                case "parent", "sibling", "child", "spouse" -> BigDecimal.valueOf(3);
                case "grandparent", "aunt", "uncle", "in-law" -> BigDecimal.ONE;
                default -> BigDecimal.ZERO;
            };
            return LeavePolicyResponse.builder().days(days).eligible(days.compareTo(BigDecimal.ZERO) > 0).build();
        });
    }

    @Override
    public Mono<LeavePolicyResponse> getMarriageLeave(LeavePolicyRequest request) {
        boolean firstMarriage = Boolean.TRUE.equals(request.getFirstMarriage());
        boolean hasMarriageCertificate = Boolean.TRUE.equals(request.getIsSpouseWorking());
        BigDecimal days = (firstMarriage && hasMarriageCertificate) ? BigDecimal.valueOf(3) : BigDecimal.ZERO;
        return Mono
                .just(LeavePolicyResponse.builder().days(days).eligible(days.compareTo(BigDecimal.ZERO) > 0).build());
    }

    @Override
    public Mono<LeavePolicyResponse> getMilitaryLeaveInfo(LeavePolicyRequest request) {
        return Mono.just(LeavePolicyResponse.builder().eligible(true).days(BigDecimal.ZERO).build());
    }

    @Override
    public Mono<LeavePolicyResponse> isHoliday(LeavePolicyRequest request) {
        if (request.getDate() == null)
            return Mono.error(new IllegalArgumentException("Date cannot be null"));
        boolean holiday = OFFICIAL_HOLIDAYS.contains(request.getDate()) ||
                request.getDate().getDayOfWeek() == DayOfWeek.SATURDAY ||
                request.getDate().getDayOfWeek() == DayOfWeek.SUNDAY;
        return Mono.just(LeavePolicyResponse.builder().eligible(holiday)
                .days(holiday ? BigDecimal.ONE : BigDecimal.ZERO).build());
    }

    @Override
    public Mono<LeavePolicyResponseList> getAllLeavePolicies() {
        return Mono.fromCallable(() -> {
            LeavePolicyResponseList list = new LeavePolicyResponseList();
            list.setLeavePolicies(List.of(
                    LeavePolicyResponse.builder().days(BigDecimal.valueOf(14)).eligible(true).build(),
                    LeavePolicyResponse.builder().days(BigDecimal.valueOf(5)).eligible(true).build(),
                    LeavePolicyResponse.builder().days(BigDecimal.valueOf(0.5)).eligible(true).build() // yarım gün
            ));
            return list;
        });
    }
}
