package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.service.EmployeeService;
import com.neg.hr.human.resource.service.impl.LeavePolicyServiceImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/leave_policies")
public class LeavePolicyController {

    private final LeavePolicyServiceImpl leavePolicyService;
    private final EmployeeService employeeService;

    public LeavePolicyController(LeavePolicyServiceImpl leavePolicyService, EmployeeService employeeService) {
        this.leavePolicyService = leavePolicyService;
        this.employeeService = employeeService;
    }

    // 1. Kıdeme göre yıllık izin
    @GetMapping("/annual-leave/{employeeId}")
    public ResponseEntity<Integer> getAnnualLeave(@PathVariable Long employeeId) {
        Employee employee = getEmployee(employeeId);
        int days = leavePolicyService.calculateAnnualLeaveDays(employee);
        return ResponseEntity.ok(days);
    }

    // 2. Yaşa bağlı +2 gün
    @GetMapping("/age-bonus/{employeeId}")
    public ResponseEntity<Integer> getAgeBasedLeaveBonus(@PathVariable Long employeeId) {
        Employee employee = getEmployee(employeeId);
        int bonus = leavePolicyService.calculateAgeBasedLeaveBonus(employee);
        return ResponseEntity.ok(bonus);
    }

    // 3. Doğum günü izni kontrolü (opsiyonel)
    @GetMapping("/birthday-leave/{employeeId}")
    public ResponseEntity<Boolean> checkBirthdayLeave(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Employee employee = getEmployee(employeeId);
        boolean eligible = leavePolicyService.isBirthdayLeaveEligible(employee, date);
        return ResponseEntity.ok(eligible);
    }

    // 4. Kadın çalışanlar için doğum izni (8+8 hafta, çoğul gebelikle +2 hafta)
    @GetMapping("/maternity-leave/{employeeId}")
    public ResponseEntity<Integer> getMaternityLeaveDays(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "false") boolean multiplePregnancy) {
        Employee employee = getEmployee(employeeId);
        int days = leavePolicyService.calculateMaternityLeaveDays(employee, multiplePregnancy);
        return ResponseEntity.ok(days);
    }

    // 5. Erkek çalışan için babalık izni (5 gün)
    @GetMapping("/paternity-leave/{employeeId}")
    public ResponseEntity<Integer> getPaternityLeaveDays(@PathVariable Long employeeId) {
        Employee employee = getEmployee(employeeId);
        int days = leavePolicyService.calculatePaternityLeaveDays(employee);
        return ResponseEntity.ok(days); // genelde sabit 5 gün
    }

    // 6. Borç izin alma uygun mu?
    @GetMapping("/can-borrow-leave/{employeeId}")
    public ResponseEntity<Boolean> canBorrowLeave(
            @PathVariable Long employeeId,
            @RequestParam int requestedDays,
            @RequestParam int currentBorrowed) {
        Employee employee = getEmployee(employeeId);
        boolean allowed = leavePolicyService.canBorrowLeave(employee, requestedDays, currentBorrowed);
        return ResponseEntity.ok(allowed);
    }

    // 7. Ölüm izni - kişi türüne göre
    @GetMapping("/bereavement-leave/{employeeId}")
    public ResponseEntity<Integer> getBereavementLeave(
            @PathVariable Long employeeId,
            @RequestParam String relationType) {
        Employee employee = getEmployee(employeeId);
        int days = leavePolicyService.calculateBereavementLeaveDays(relationType);
        return ResponseEntity.ok(days);
    }

    // 8. Evlilik izni
    @GetMapping("/marriage-leave/{employeeId}")
    public ResponseEntity<Integer> getMarriageLeave(
            @PathVariable Long employeeId,
            @RequestParam boolean isFirstMarriage,
            @RequestParam boolean isSpouseWorking) {
        Employee employee = getEmployee(employeeId);
        int days = leavePolicyService.calculateMarriageLeaveDays(employee, isFirstMarriage, isSpouseWorking);
        return ResponseEntity.ok(days);
    }


    // 9. Askerlik izni - sabit ücretsiz izin
    @GetMapping("/military-leave/{employeeId}")
    public ResponseEntity<String> getMilitaryLeaveInfo(@PathVariable Long employeeId) {
        Employee employee = getEmployee(employeeId);
        boolean eligible = leavePolicyService.isEligibleForMilitaryLeave(employee);
        return ResponseEntity.ok(eligible ? "Eligible for unpaid military leave" : "Not eligible");
    }

    // 10. Resmi ve dini bayramlarda izinli mi?
    @GetMapping("/holiday-leave/{employeeId}")
    public ResponseEntity<Boolean> isHoliday(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean isHoliday = leavePolicyService.isOfficialHoliday(date);
        return ResponseEntity.ok(isHoliday);
    }

    // 11. Yardımcı metod
    private Employee getEmployee(Long employeeId) {
        return employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
