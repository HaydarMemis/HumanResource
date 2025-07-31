package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.business.LeaveRequestValidator;
import com.neg.hr.human.resource.dto.CreateLeaveBalanceDTO;
import com.neg.hr.human.resource.dto.CreateLeaveRequestDTO;
import com.neg.hr.human.resource.dto.LeaveRequestDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveRequest;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveBalanceMapper;
import com.neg.hr.human.resource.mapper.LeaveRequestMapper;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import com.neg.hr.human.resource.service.impl.LeaveRequestServiceImpl;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestServiceImpl leaveRequestService;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestValidator leaveRequestValidator;

    public LeaveRequestController(LeaveRequestServiceImpl leaveRequestService, EmployeeRepository employeeRepository, LeaveTypeRepository leaveTypeRepository) {
        this.leaveRequestService = leaveRequestService;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveRequestValidator = new LeaveRequestValidator();
    }

    @GetMapping
    public List<LeaveRequestDTO> getAllLeaveRequests() {
        return leaveRequestService.findAll()
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestDTO> getLeaveRequestById(@PathVariable Long id) {
        return leaveRequestService.findById(id)
                .map(LeaveRequestMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LeaveRequestDTO> createLeaveRequest(@Valid @RequestBody CreateLeaveRequestDTO dto) {
        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type ID"));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        Employee approver = employeeRepository.findById(dto.getApprovedById())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        LeaveRequest leaveRequest = LeaveRequestMapper.toEntity(dto, employee, leaveType, approver);
        LeaveRequest savedLeaveRequest = leaveRequestService.save(leaveRequest);

        return ResponseEntity.ok(LeaveRequestMapper.toDTO(savedLeaveRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequest> updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest leaveRequest) {
        LeaveRequest updated = leaveRequestService.update(id, leaveRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long id) {
        leaveRequestService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequest> getLeaveRequestsByEmployee(@PathVariable Long employeeId) {
        return leaveRequestService.findByEmployeeId(employeeId);
    }

    @GetMapping("/status/{status}")
    public List<LeaveRequest> getLeaveRequestsByStatus(@PathVariable String status) {
        return leaveRequestService.findByStatus(status);
    }

    @GetMapping("/cancelled")
    public List<LeaveRequest> getCancelledLeaveRequests() {
        return leaveRequestService.findByIsCancelledTrue();
    }

    @GetMapping("/approver/{approverId}")
    public List<LeaveRequest> getLeaveRequestsByApprover(@PathVariable Long approverId) {
        return leaveRequestService.findByApprovedById(approverId);
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    public List<LeaveRequest> getLeaveRequestsByEmployeeAndStatus(@PathVariable Long employeeId, @PathVariable String status) {
        return leaveRequestService.findByEmployeeIdAndStatus(employeeId, status);
    }

    @GetMapping("/date-range")
    public List<LeaveRequest> getLeaveRequestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findByStartDateBetween(start, end);
    }

    @GetMapping("/employee/{employeeId}/leave-type/{leaveTypeId}/date-range")
    public List<LeaveRequest> getLeaveRequestsByEmployeeLeaveTypeAndDateRange(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(employeeId, leaveTypeId, start, end);
    }

    @GetMapping("/overlapping")
    public List<LeaveRequest> getOverlappingLeaveRequests(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findOverlappingRequests(employeeId, start, end);
    }
}
