package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.LeavePolicy.LeavePolicyRequestDTO;
import com.neg.hr.human.resource.dto.LeavePolicy.LeavePolicyResponseDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.service.EmployeeService;
import com.neg.hr.human.resource.service.LeavePolicyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leave_policies")
public class LeavePolicyController {

    private final LeavePolicyService leavePolicyService;
    private final EmployeeService employeeService;

    public LeavePolicyController(LeavePolicyService leavePolicyService, EmployeeService employeeService) {
        this.leavePolicyService = leavePolicyService;
        this.employeeService = employeeService;
    }

    @PostMapping("/annual-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getAnnualLeave(@Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int days = leavePolicyService.calculateAnnualLeaveDays(employee);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/age-bonus")
    public ResponseEntity<LeavePolicyResponseDTO> getAgeBasedLeaveBonus(@Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int bonus = leavePolicyService.calculateAgeBasedLeaveBonus(employee);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(bonus)
                .eligible(bonus > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/birthday-leave")
    public ResponseEntity<LeavePolicyResponseDTO> checkBirthdayLeave(@Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        boolean eligible = leavePolicyService.isBirthdayLeaveEligible(employee, request.getDate());

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .eligible(eligible)
                .days(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/maternity-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getMaternityLeaveDays(@Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        boolean multiplePregnancy = request.getMultiplePregnancy() != null && request.getMultiplePregnancy();

        int days = leavePolicyService.calculateMaternityLeaveDays(employee, multiplePregnancy);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/paternity-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getPaternityLeaveDays(@Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());
        int days = leavePolicyService.calculatePaternityLeaveDays(employee);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/can-borrow-leave")
    public ResponseEntity<LeavePolicyResponseDTO> canBorrowLeave(@Valid @RequestBody LeavePolicyRequestDTO request) {
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

    @PostMapping("/bereavement-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getBereavementLeave(@Valid @RequestBody LeavePolicyRequestDTO request) {
        String relationType = request.getRelationType();
        int days = leavePolicyService.calculateBereavementLeaveDays(relationType);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .days(days)
                .eligible(days > 0)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/marriage-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getMarriageLeave(@Valid @RequestBody LeavePolicyRequestDTO request) {
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

    @PostMapping("/military-leave")
    public ResponseEntity<LeavePolicyResponseDTO> getMilitaryLeaveInfo(@Valid @RequestBody LeavePolicyRequestDTO request) {
        Employee employee = getEmployee(request.getEmployeeId());

        boolean eligible = leavePolicyService.isEligibleForMilitaryLeave(employee);

        LeavePolicyResponseDTO response = LeavePolicyResponseDTO.builder()
                .eligible(eligible)
                .days(null)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/holiday-leave")
    public ResponseEntity<LeavePolicyResponseDTO> isHoliday(@Valid @RequestBody LeavePolicyRequestDTO request) {
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
