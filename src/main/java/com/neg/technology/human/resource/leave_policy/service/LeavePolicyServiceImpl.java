package com.neg.technology.human.resource.leave_policy.service;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.service.EmployeeService;
import com.neg.technology.human.resource.leave_policy.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.leave_policy.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.leave_policy.model.response.LeavePolicyResponseList;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Optional;
import java.util.Set;

@Service
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

    public LeavePolicyServiceImpl(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    private Employee getEmployee(Long employeeId) {
        return (Employee) employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Override
    public Optional<Employee> getEmployeeEntityById(Long id) {
        return employeeService.findEntityById(id);
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

    @Override
    public ResponseEntity<LeavePolicyResponse> getAnnualLeave(LeavePolicyRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int days;
        int yearsWorked = calculateYearsBetween(getEmploymentStartDate(employee), LocalDate.now());

        if (yearsWorked < 1) days = 0;
        else if (yearsWorked < 5) days = calculateAge(employee) >= 50 ? 20 : 14;
        else if (yearsWorked < 15) days = 20;
        else days = 26;

        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .days(days)
                .eligible(days > 0)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> getAgeBasedLeaveBonus(LeavePolicyRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int age = calculateAge(employee);
        int bonus = age >= 50 ? 2 : 0;
        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .days(bonus)
                .eligible(bonus > 0)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> checkBirthdayLeave(LeavePolicyRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        LocalDate date = request.getDate();
        boolean eligible = getBirthDate(employee) != null &&
                getBirthDate(employee).getMonth() == date.getMonth() &&
                getBirthDate(employee).getDayOfMonth() == date.getDayOfMonth();
        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .eligible(eligible)
                .days(null)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> getMaternityLeaveDays(LeavePolicyRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        boolean multiplePregnancy = request.getMultiplePregnancy() != null && request.getMultiplePregnancy();
        int days = "female".equalsIgnoreCase(getGender(employee)) ? (multiplePregnancy ? 140 : 112) : 0;
        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .days(days)
                .eligible(days > 0)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> getPaternityLeaveDays(LeavePolicyRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int days = "male".equalsIgnoreCase(getGender(employee)) ? 5 : 0;
        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .days(days)
                .eligible(days > 0)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> canBorrowLeave(LeavePolicyRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        Integer requestedDays = request.getRequestedDays();
        Integer currentBorrowed = request.getCurrentBorrowed();

        boolean allowed = false;
        if (requestedDays != null && currentBorrowed != null) {
            LocalDate startDate = getEmploymentStartDate(employee);
            int monthsWorked = Period.between(startDate, LocalDate.now()).getMonths() +
                    Period.between(startDate, LocalDate.now()).getYears() * 12;
            allowed = monthsWorked >= 3 && requestedDays <= 5 && currentBorrowed + requestedDays <= 10;
        }

        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .eligible(allowed)
                .days(null)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> getBereavementLeave(LeavePolicyRequest request) {
        int days = switch (request.getRelationType() != null ? request.getRelationType().toLowerCase() : "") {
            case "parent", "sibling", "child", "spouse" -> 3;
            case "grandparent", "aunt", "uncle", "in-law" -> 1;
            default -> 0;
        };
        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .days(days)
                .eligible(days > 0)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> getMarriageLeave(LeavePolicyRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        boolean firstMarriage = request.getFirstMarriage() != null && request.getFirstMarriage();
        boolean hasMarriageCertificate = request.getIsSpouseWorking() != null && request.getIsSpouseWorking();
        int days = (firstMarriage && hasMarriageCertificate) ? 3 : 0;
        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .days(days)
                .eligible(days > 0)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> getMilitaryLeaveInfo(LeavePolicyRequest request) {
        Employee employee = getEmployee(request.getEmployeeId());
        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .eligible(true)
                .days(null)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponse> isHoliday(LeavePolicyRequest request) {
        LocalDate date = request.getDate();
        boolean holiday = OFFICIAL_HOLIDAYS.contains(date) ||
                date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY;
        return ResponseEntity.ok(LeavePolicyResponse.builder()
                .eligible(holiday)
                .days(null)
                .build());
    }

    @Override
    public ResponseEntity<LeavePolicyResponseList> getAllLeavePolicies() {
        LeavePolicyResponseList list = new LeavePolicyResponseList();
        list.setLeavePolicies(java.util.List.of(
                LeavePolicyResponse.builder().days(14).eligible(true).build(),
                LeavePolicyResponse.builder().days(5).eligible(true).build()
        ));
        return ResponseEntity.ok(list);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.empty();
    }
}
