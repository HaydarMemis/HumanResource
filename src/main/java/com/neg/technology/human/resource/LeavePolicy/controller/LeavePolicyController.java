package com.neg.technology.human.resource.LeavePolicy.controller;

import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Employee.service.EmployeeService;
import com.neg.technology.human.resource.LeavePolicy.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.LeavePolicy.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.LeavePolicy.model.response.LeavePolicyResponseList;
import com.neg.technology.human.resource.LeavePolicy.service.LeavePolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "LeavePolicy Controller", description = "Operations related to leave policies")
@RestController
@RequestMapping("/api/leave_policies")
public class LeavePolicyController {

    private final LeavePolicyService leavePolicyService;
    private final EmployeeService employeeService;

    public LeavePolicyController(LeavePolicyService leavePolicyService, EmployeeService employeeService) {
        this.leavePolicyService = leavePolicyService;
        this.employeeService = employeeService;
    }

    @Operation(summary = "Get annual leave days")
    @ApiResponse(responseCode = "200", description = "Annual leave days calculated")
    @PostMapping("/annual-leave")
    public ResponseEntity<LeavePolicyResponse> getAnnualLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getAnnualLeave(request);
    }

    @Operation(summary = "Get age-based leave bonus days")
    @PostMapping("/age-bonus")
    public ResponseEntity<LeavePolicyResponse> getAgeBasedLeaveBonus(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getAgeBasedLeaveBonus(request);
    }

    @Operation(summary = "Check birthday leave eligibility")
    @PostMapping("/birthday-leave")
    public ResponseEntity<LeavePolicyResponse> checkBirthdayLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.checkBirthdayLeave(request);
    }

    @Operation(summary = "Get maternity leave days")
    @PostMapping("/maternity-leave")
    public ResponseEntity<LeavePolicyResponse> getMaternityLeaveDays(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getMaternityLeaveDays(request);
    }

    @Operation(summary = "Get paternity leave days")
    @PostMapping("/paternity-leave")
    public ResponseEntity<LeavePolicyResponse> getPaternityLeaveDays(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getPaternityLeaveDays(request);
    }

    @Operation(summary = "Check if employee can borrow leave days")
    @PostMapping("/can-borrow-leave")
    public ResponseEntity<LeavePolicyResponse> canBorrowLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.canBorrowLeave(request);
    }

    @Operation(summary = "Get bereavement leave days")
    @PostMapping("/bereavement-leave")
    public ResponseEntity<LeavePolicyResponse> getBereavementLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getBereavementLeave(request);
    }

    @Operation(summary = "Get marriage leave days")
    @PostMapping("/marriage-leave")
    public ResponseEntity<LeavePolicyResponse> getMarriageLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getMarriageLeave(request);
    }

    @Operation(summary = "Check military leave eligibility")
    @PostMapping("/military-leave")
    public ResponseEntity<LeavePolicyResponse> getMilitaryLeaveInfo(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getMilitaryLeaveInfo(request);
    }

    @Operation(summary = "Check if given date is official holiday")
    @PostMapping("/holiday-leave")
    public ResponseEntity<LeavePolicyResponse> isHoliday(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.isHoliday(request);
    }

    @Operation(summary = "Get all leave policies")
    @PostMapping("/all")
    public ResponseEntity<LeavePolicyResponseList> getAllLeavePolicies() {
        return leavePolicyService.getAllLeavePolicies();
    }

    private Employee getEmployee(Long employeeId) {
        return (Employee) employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
