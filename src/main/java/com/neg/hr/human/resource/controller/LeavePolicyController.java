package com.neg.hr.human.resouce.controller;

import com.neg.hr.human.resouce.entity.Employee;
import com.neg.hr.human.resouce.service.EmployeeService;
import com.neg.hr.human.resouce.service.LeavePolicyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/leave_policies")
public class LeavePolicyController {

    private final LeavePolicyService leavePolicyService;
    private final EmployeeService employeeService;

    public LeavePolicyController(LeavePolicyService leavePolicyService, EmployeeService employeeService) {
        this.leavePolicyService = leavePolicyService;
        this.employeeService = employeeService;
    }

    @GetMapping("/annual-leave/{employeeId}")
    public ResponseEntity<Integer> getAnnualLeave(@PathVariable Long employeeId) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        int days = leavePolicyService.calculateAnnualLeaveDays(employee);
        return ResponseEntity.ok(days);
    }

    @GetMapping("/age-bonus/{employeeId}")
    public ResponseEntity<Integer> getAgeBasedLeaveBonus(@PathVariable Long employeeId) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        int bonus = leavePolicyService.calculateAgeBasedLeaveBonus(employee);
        return ResponseEntity.ok(bonus);
    }

    @GetMapping("/birthday-leave/{employeeId}")
    public ResponseEntity<Boolean> checkBirthdayLeave(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        boolean eligible = leavePolicyService.isBirthdayLeaveEligible(employee, date);
        return ResponseEntity.ok(eligible);
    }

    @GetMapping("/maternity-leave/{employeeId}")
    public ResponseEntity<Integer> getMaternityLeaveDays(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "false") boolean multiplePregnancy) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        int days = leavePolicyService.calculateMaternityLeaveDays(employee, multiplePregnancy);
        return ResponseEntity.ok(days);
    }

    @GetMapping("/can-borrow-leave/{employeeId}")
    public ResponseEntity<Boolean> canBorrowLeave(
            @PathVariable Long employeeId,
            @RequestParam int requestedDays,
            @RequestParam int currentBorrowed) {
        Employee employee = employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        boolean allowed = leavePolicyService.canBorrowLeave(employee, requestedDays, currentBorrowed);
        return ResponseEntity.ok(allowed);
    }

}
