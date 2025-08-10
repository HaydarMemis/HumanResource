package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.*;
import com.neg.hr.human.resource.dto.create.CreateLeaveRequestRequestDTO;
import com.neg.hr.human.resource.dto.entity.LeaveRequestEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveRequestRequestDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveRequest;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveRequestMapper;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import com.neg.hr.human.resource.service.LeaveRequestService;
import com.neg.hr.human.resource.validator.LeaveRequestValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestValidator validator;

    public LeaveRequestController(LeaveRequestService leaveRequestService,
                                  EmployeeRepository employeeRepository,
                                  LeaveTypeRepository leaveTypeRepository,
                                  LeaveRequestValidator validator) {
        this.leaveRequestService = leaveRequestService;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.validator = validator;
    }

    // POST - get all leave requests
    @PostMapping("/getAll")
    public List<LeaveRequestEntityDTO> getAllLeaveRequests() {
        return leaveRequestService.findAll()
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // POST - get leave request by ID
    @PostMapping("/getById")
    public ResponseEntity<LeaveRequestEntityDTO> getLeaveRequestById(@Valid @RequestBody IdRequest request) {
        return leaveRequestService.findById(request.getId())
                .map(LeaveRequestMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - create new leave request
    @PostMapping("/create")
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

    // POST - update leave request
    @PostMapping("/update")
    public ResponseEntity<LeaveRequestEntityDTO> updateLeaveRequest(@Valid @RequestBody UpdateLeaveRequestRequestDTO dto) {
        if (!leaveRequestService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }

        validator.validateUpdateDTO(dto);

        LeaveRequest existing = leaveRequestService.findById(dto.getId()).get();

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

    // POST - delete leave request
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveRequest(@Valid @RequestBody IdRequest request) {
        if (!leaveRequestService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        leaveRequestService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    // === Filtered Queries ===

    // POST - get leave requests by employee
    @PostMapping("/getByEmployee")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByEmployee(@Valid @RequestBody IdRequest request) {
        return leaveRequestService.findByEmployeeId(request.getId())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // POST - get leave requests by status
    @PostMapping("/getByStatus")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByStatus(@Valid @RequestBody StatusRequest request) {
        return leaveRequestService.findByStatus(request.getStatus())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // POST - get cancelled leave requests
    @PostMapping("/getCancelled")
    public List<LeaveRequestEntityDTO> getCancelledLeaveRequests() {
        return leaveRequestService.findByIsCancelledTrue()
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // POST - get leave requests by approver
    @PostMapping("/getByApprover")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByApprover(@Valid @RequestBody IdRequest request) {
        return leaveRequestService.findByApprovedById(request.getId())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // POST - get leave requests by employee and status
    @PostMapping("/getByEmployeeAndStatus")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByEmployeeAndStatus(
            @Valid @RequestBody EmployeeStatusRequest request) {
        return leaveRequestService.findByEmployeeIdAndStatus(request.getEmployeeId(), request.getStatus())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // POST - get leave requests by date range
    @PostMapping("/getByDateRange")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByDateRange(
            @Valid @RequestBody DateRangeRequest request) {
        return leaveRequestService.findByStartDateBetween(request.getStartDate(), request.getEndDate())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // POST - get leave requests by employee, leave type and date range
    @PostMapping("/getByEmployeeLeaveTypeAndDateRange")
    public List<LeaveRequestEntityDTO> getLeaveRequestsByEmployeeLeaveTypeAndDateRange(
            @Valid @RequestBody EmployeeLeaveTypeDateRangeRequest request) {
        return leaveRequestService.findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(
                        request.getEmployeeId(),
                        request.getLeaveTypeId(),
                        request.getStartDate(),
                        request.getEndDate())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }

    // POST - get overlapping leave requests
    @PostMapping("/getOverlapping")
    public List<LeaveRequestEntityDTO> getOverlappingLeaveRequests(
            @Valid @RequestBody EmployeeDateRangeRequest request) {
        return leaveRequestService.findOverlappingRequests(
                        request.getEmployeeId(),
                        request.getStartDate(),
                        request.getEndDate())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
    }
}