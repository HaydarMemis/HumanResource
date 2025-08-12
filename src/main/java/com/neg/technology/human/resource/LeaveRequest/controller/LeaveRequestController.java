package com.neg.technology.human.resource.LeaveRequest.controller;

import com.neg.technology.human.resource.Employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.Employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeDateRangeRequest;
import com.neg.technology.human.resource.LeaveRequest.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.LeaveRequest.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.LeaveRequest.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.Utility.request.DateRangeRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.StatusRequest;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.LeaveRequest.model.entity.LeaveRequest;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import com.neg.technology.human.resource.LeaveRequest.model.mapper.LeaveRequestMapper;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.LeaveType.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.LeaveRequest.service.LeaveRequestService;
import com.neg.technology.human.resource.LeaveRequest.validator.LeaveRequestValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "LeaveRequest Controller", description = "Operations related to leave requests management")
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

    @Operation(summary = "Get all leave requests", description = "Retrieve all leave requests")
    @ApiResponse(responseCode = "200", description = "List of leave requests retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<LeaveRequestResponse>> getAllLeaveRequests() {
        List<LeaveRequestResponse> list = leaveRequestService.findAll()
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave request by ID", description = "Retrieve a leave request by its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave request found"),
            @ApiResponse(responseCode = "404", description = "Leave request not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<LeaveRequestResponse> getLeaveRequestById(
            @Parameter(description = "ID of the leave request", required = true)
            @Valid @RequestBody IdRequest request) {
        return leaveRequestService.findById(request.getId())
                .map(LeaveRequestMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new leave request", description = "Create a new leave request record")
    @ApiResponse(responseCode = "200", description = "Leave request created successfully")
    @PostMapping("/create")
    public ResponseEntity<LeaveRequestResponse> createLeaveRequest(
            @Parameter(description = "Leave request data for creation", required = true)
            @Valid @RequestBody CreateLeaveRequestRequest dto) {

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

    @Operation(summary = "Update an existing leave request", description = "Update details of a leave request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave request updated successfully"),
            @ApiResponse(responseCode = "404", description = "Leave request not found")
    })
    @PostMapping("/update")
    public ResponseEntity<LeaveRequestResponse> updateLeaveRequest(
            @Parameter(description = "Leave request data for update", required = true)
            @Valid @RequestBody UpdateLeaveRequestRequest dto) {

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

    @Operation(summary = "Delete a leave request", description = "Delete a leave request by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Leave request deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Leave request not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveRequest(
            @Parameter(description = "ID of the leave request to delete", required = true)
            @Valid @RequestBody IdRequest request) {
        if (!leaveRequestService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        leaveRequestService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get leave requests by employee ID", description = "Retrieve leave requests for a specific employee")
    @ApiResponse(responseCode = "200", description = "List of leave requests for employee retrieved successfully")
    @PostMapping("/getByEmployee")
    public ResponseEntity<List<LeaveRequestResponse>> getLeaveRequestsByEmployee(
            @Parameter(description = "Employee ID", required = true)
            @Valid @RequestBody IdRequest request) {
        List<LeaveRequestResponse> list = leaveRequestService.findByEmployeeId(request.getId())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave requests by status", description = "Retrieve leave requests filtered by status")
    @ApiResponse(responseCode = "200", description = "List of leave requests filtered by status retrieved successfully")
    @PostMapping("/getByStatus")
    public ResponseEntity<List<LeaveRequestResponse>> getLeaveRequestsByStatus(
            @Parameter(description = "Status to filter", required = true)
            @Valid @RequestBody StatusRequest request) {
        List<LeaveRequestResponse> list = leaveRequestService.findByStatus(request.getStatus())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get cancelled leave requests", description = "Retrieve leave requests that are cancelled")
    @ApiResponse(responseCode = "200", description = "List of cancelled leave requests retrieved successfully")
    @PostMapping("/getCancelled")
    public ResponseEntity<List<LeaveRequestResponse>> getCancelledLeaveRequests() {
        List<LeaveRequestResponse> list = leaveRequestService.findByIsCancelledTrue()
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave requests by approver ID", description = "Retrieve leave requests approved by a specific approver")
    @ApiResponse(responseCode = "200", description = "List of leave requests approved by the approver retrieved successfully")
    @PostMapping("/getByApprover")
    public ResponseEntity<List<LeaveRequestResponse>> getLeaveRequestsByApprover(
            @Parameter(description = "Approver ID", required = true)
            @Valid @RequestBody IdRequest request) {
        List<LeaveRequestResponse> list = leaveRequestService.findByApprovedById(request.getId())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave requests by employee and status", description = "Retrieve leave requests filtered by employee ID and status")
    @ApiResponse(responseCode = "200", description = "List of leave requests filtered by employee and status retrieved successfully")
    @PostMapping("/getByEmployeeAndStatus")
    public ResponseEntity<List<LeaveRequestResponse>> getLeaveRequestsByEmployeeAndStatus(
            @Parameter(description = "Employee ID and status filter", required = true)
            @Valid @RequestBody EmployeeStatusRequest request) {
        List<LeaveRequestResponse> list = leaveRequestService.findByEmployeeIdAndStatus(request.getEmployeeId(), request.getStatus())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave requests by date range", description = "Retrieve leave requests whose start date is between given dates")
    @ApiResponse(responseCode = "200", description = "List of leave requests filtered by date range retrieved successfully")
    @PostMapping("/getByDateRange")
    public ResponseEntity<List<LeaveRequestResponse>> getLeaveRequestsByDateRange(
            @Parameter(description = "Date range filter", required = true)
            @Valid @RequestBody DateRangeRequest request) {
        List<LeaveRequestResponse> list = leaveRequestService.findByStartDateBetween(request.getStartDate(), request.getEndDate())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave requests by employee, leave type and date range", description = "Retrieve leave requests filtered by employee ID, leave type ID and date range")
    @ApiResponse(responseCode = "200", description = "List of leave requests filtered by employee, leave type and date range retrieved successfully")
    @PostMapping("/getByEmployeeLeaveTypeAndDateRange")
    public ResponseEntity<List<LeaveRequestResponse>> getLeaveRequestsByEmployeeLeaveTypeAndDateRange(
            @Parameter(description = "Filter by employee ID, leave type ID and date range", required = true)
            @Valid @RequestBody EmployeeLeaveTypeDateRangeRequest request) {
        List<LeaveRequestResponse> list = leaveRequestService.findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(
                        request.getEmployeeId(),
                        request.getLeaveTypeId(),
                        request.getStartDate(),
                        request.getEndDate())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get overlapping leave requests", description = "Retrieve leave requests overlapping given employee and date range")
    @ApiResponse(responseCode = "200", description = "List of overlapping leave requests retrieved successfully")
    @PostMapping("/getOverlapping")
    public ResponseEntity<List<LeaveRequestResponse>> getOverlappingLeaveRequests(
            @Parameter(description = "Employee ID and date range for overlap check", required = true)
            @Valid @RequestBody EmployeeDateRangeRequest request) {
        List<LeaveRequestResponse> list = leaveRequestService.findOverlappingRequests(
                        request.getEmployeeId(),
                        request.getStartDate(),
                        request.getEndDate())
                .stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }
}
