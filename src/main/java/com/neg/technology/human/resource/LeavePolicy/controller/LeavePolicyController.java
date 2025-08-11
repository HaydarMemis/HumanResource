package com.neg.technology.human.resource.LeavePolicy.controller;

import com.neg.technology.human.resource.dto.LeavePolicy.LeavePolicyRequestDTO;
import com.neg.technology.human.resource.dto.LeavePolicy.LeavePolicyResponseDTO;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Employee.service.EmployeeService;
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

    @Operation(summary = "Get annual leave days", description = "Calculate annual leave days for an employee")
    @ApiResponse(responseCode = "200", description = "Annual leave days calculated")
    @PostMapping("/annual-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getAnnualLeave(
            @Parameter(description = "Employee ID request", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int days = leavePolicyService.calculateAnnualLeaveDays(employee);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get age-based leave bonus days", description = "Calculate leave bonus based on employee age")
    @ApiResponse(responseCode = "200", description = "Age-based leave bonus calculated")
    @PostMapping("/age-bonus")
    public ResponseEntity<LeavePolicyResponseDTO> getAgeBasedLeaveBonus(
            @Parameter(description = "Employee ID request", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int bonus = leavePolicyService.calculateAnnualLeaveDays(employee); // <-- Bu satır kontrol edilmeli, age bonus ile mi ilgili?

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(bonus)
                .eligible(bonus > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check birthday leave eligibility", description = "Check if employee is eligible for birthday leave on a given date")
    @ApiResponse(responseCode = "200", description = "Birthday leave eligibility checked")
    @PostMapping("/birthday-leave")
    public ResponseEntity<LeavePolicyResponseDTO> checkBirthdayLeave(
            @Parameter(description = "Employee ID and date request", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        boolean eligible = leavePolicyService.isBirthdayLeaveEligible(employee, request.getDate());

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .eligible(eligible)
                .days(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get maternity leave days", description = "Calculate maternity leave days considering multiple pregnancy")
    @ApiResponse(responseCode = "200", description = "Maternity leave days calculated")
    @PostMapping("/maternity-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getMaternityLeaveDays(
            @Parameter(description = "Employee ID and pregnancy info", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        boolean multiplePregnancy = request.getMultiplePregnancy() != null && request.getMultiplePregnancy();

        int days = leavePolicyService.calculateMaternityLeaveDays(employee, multiplePregnancy);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get paternity leave days", description = "Calculate paternity leave days for an employee")
    @ApiResponse(responseCode = "200", description = "Paternity leave days calculated")
    @PostMapping("/paternity-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getPaternityLeaveDays(
            @Parameter(description = "Employee ID request", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int days = leavePolicyService.calculatePaternityLeaveDays(employee);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check if employee can borrow leave days", description = "Determine if leave borrowing is allowed")
    @ApiResponse(responseCode = "200", description = "Leave borrowing eligibility checked")
    @PostMapping("/can-borrow-leave")
    public ResponseEntity<LeavePolicyResponseDTO> canBorrowLeave(
            @Parameter(description = "Leave borrowing request data", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());

        Integer requestedDays = request.getRequestedDays();
        Integer currentBorrowed = request.getCurrentBorrowed();

        boolean allowed = false;
        if (requestedDays != null && currentBorrowed != null) {
            allowed = leavePolicyService.canBorrowLeave(employee, requestedDays, currentBorrowed);
        }

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .eligible(allowed)
                .days(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get bereavement leave days", description = "Calculate bereavement leave days based on relation type")
    @ApiResponse(responseCode = "200", description = "Bereavement leave days calculated")
    @PostMapping("/bereavement-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getBereavementLeave(
            @Parameter(description = "Bereavement leave request data", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        String relationType = request.getRelationType();
        int days = leavePolicyService.calculateBereavementLeaveDays(relationType);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get marriage leave days", description = "Calculate marriage leave days based on employee and spouse info")
    @ApiResponse(responseCode = "200", description = "Marriage leave days calculated")
    @PostMapping("/marriage-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getMarriageLeave(
            @Parameter(description = "Marriage leave request data", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());

        Boolean firstMarriage = request.getFirstMarriage();
        Boolean isSpouseWorking = request.getIsSpouseWorking();

        boolean hasMarriageCertificate = isSpouseWorking != null && isSpouseWorking;

        int days = leavePolicyService.calculateMarriageLeaveDays(employee,
                firstMarriage != null && firstMarriage,
                hasMarriageCertificate);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check military leave eligibility", description = "Check if employee is eligible for military leave")
    @ApiResponse(responseCode = "200", description = "Military leave eligibility checked")
    @PostMapping("/military-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getMilitaryLeaveInfo(
            @Parameter(description = "Employee ID request", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());

        boolean eligible = leavePolicyService.isEligibleForMilitaryLeave(employee);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .eligible(eligible)
                .days(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check if given date is official holiday", description = "Determine if the specified date is an official holiday")
    @ApiResponse(responseCode = "200", description = "Holiday status checked")
    @PostMapping("/holiday-leave")
    public ResponseEntity<LeavePolicyResponseDTO> isHoliday(
            @Parameter(description = "Date request", required = true)
            @Valid @RequestBody LeavePolicyRequestDTO request) {
        boolean isHoliday = leavePolicyService.isOfficialHoliday(request.getDate());

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .eligible(isHoliday)
                .days(null)
                .build();

        return ResponseEntity.ok(response);
    }

    private Employee getEmployee(Long employeeId) {
        return employeeService.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }
}
