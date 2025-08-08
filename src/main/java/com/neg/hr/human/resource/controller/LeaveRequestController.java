package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.validator.LeaveRequestValidator;
import com.neg.hr.human.resource.dto.create.CreateLeaveRequestRequestDTO;
import com.neg.hr.human.resource.dto.LeaveRequestEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveRequestRequestDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveRequest;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveRequestMapper;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import com.neg.hr.human.resource.service.impl.LeaveRequestServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestServiceImpl leaveRequestService;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestValidator validator;

    // GET all leave requests
    @GetMapping
    public List<LeaveRequestEntityDTO> getAllLeaveRequests() {
        return leaveRequestService.findAll()
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // GET leave request by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequestEntityDTO> getLeaveRequestById(@PathVariable Long id) {
        return leaveRequestService.findById(id)
                .map(LeaveRequestMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST create new leave request
    @PostMapping
    public ResponseEntity<LeaveRequestEntityDTO> createLeaveRequest(@Valid @RequestBody CreateLeaveRequestRequestDTO dto) {
        validator.validateCreateDTO(dto);

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type ID"));

        Employee approver = employeeRepository.findById(dto.getApprovedById())
                .orElseThrow(() -> new IllegalArgumentException("Invalid approver ID"));

        LeaveRequest entity = LeaveRequestMapper.toEntity(dto, employee, leaveType, approver);
        LeaveRequest saved = leaveRequestService.save(entity);
        return ResponseEntity.ok(LeaveRequestMapper.toDTO(saved));
    }

    // PUT update leave request
    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequestEntityDTO> updateLeaveRequest(@PathVariable Long id, @Valid @RequestBody UpdateLeaveRequestRequestDTO dto) {
        validator.validateUpdateDTO(dto);

        LeaveRequest existing = leaveRequestService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));

        Employee employee = (dto.getEmployeeId() != null)
                ? employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"))
                : null;

        LeaveType leaveType = (dto.getLeaveTypeId() != null)
                ? leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type ID"))
                : null;

        Employee approver = (dto.getApprovedById() != null)
                ? employeeRepository.findById(dto.getApprovedById())
                .orElseThrow(() -> new IllegalArgumentException("Invalid approver ID"))
                : null;

        LeaveRequestMapper.updateEntity(existing, dto, employee, leaveType, approver);
        LeaveRequest updated = leaveRequestService.save(existing);
        return ResponseEntity.ok(LeaveRequestMapper.toDTO(updated));
    }

    // DELETE leave request
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long id) {
        leaveRequestService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // === Filtered Queries ===

    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByEmployee(@PathVariable Long employeeId) {
        return leaveRequestService.findByEmployeeId(employeeId)
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }

    @GetMapping("/status/{status}")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByStatus(@PathVariable String status) {
        return leaveRequestService.findByStatus(status)
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }

    @GetMapping("/cancelled")
    public List<LeaveRequestEntityDTO> getCancelledLeaveRequests() {
        return leaveRequestService.findByIsCancelledTrue()
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }

    @GetMapping("/approver/{approverId}")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByApprover(@PathVariable Long approverId) {
        return leaveRequestService.findByApprovedById(approverId)
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByEmployeeAndStatus(@PathVariable Long employeeId, @PathVariable String status) {
        return leaveRequestService.findByEmployeeIdAndStatus(employeeId, status)
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }

    @GetMapping("/date-range")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findByStartDateBetween(start, end)
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }

    @GetMapping("/employee/{employeeId}/leave-type/{leaveTypeId}/date-range")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByEmployeeLeaveTypeAndDateRange(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(employeeId, leaveTypeId, start, end)
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }

    @GetMapping("/overlapping")
    public List<LeaveRequestEntityDTO> getOverlappingLeaveRequests(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findOverlappingRequests(employeeId, start, end)
                .stream().map(LeaveRequestMapper::toDTO).toList();
    }
}
