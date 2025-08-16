package com.neg.technology.human.resource.leave_request.controller;

import com.neg.technology.human.resource.leave_request.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave_request.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.leave_request.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.leave_request.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.leave_request.service.LeaveRequestService;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.StatusRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.leave_type.model.request.EmployeeLeaveTypeDateRangeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "LeaveRequest Controller", description = "Operations related to leave requests management")
@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @Operation(summary = "Get all leave requests")
    @PostMapping("/getAll")
    public ResponseEntity<LeaveRequestResponseList> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAll());
    }

    @Operation(summary = "Get leave request by ID")
    @ApiResponse(responseCode = "200", description = "Leave request found")
    @ApiResponse(responseCode = "404", description = "Leave request not found")
    @PostMapping("/getById")
    public ResponseEntity<LeaveRequestResponse> getLeaveRequestById(@Valid @RequestBody IdRequest request) {
        LeaveRequestResponse dto = leaveRequestService.getById(request);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Create a new leave request")
    @PostMapping("/create")
    public ResponseEntity<LeaveRequestResponse> createLeaveRequest(@Valid @RequestBody CreateLeaveRequestRequest dto) {
        return ResponseEntity.ok(leaveRequestService.create(dto));
    }

    @Operation(summary = "Update an existing leave request")
    @PostMapping("/update")
    public ResponseEntity<LeaveRequestResponse> updateLeaveRequest(@Valid @RequestBody UpdateLeaveRequestRequest dto) {
        LeaveRequestResponse response = leaveRequestService.update(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a leave request")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveRequest(@Valid @RequestBody IdRequest request) {
        leaveRequestService.delete(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get leave requests by employee ID")
    @PostMapping("/getByEmployee")
    public ResponseEntity<LeaveRequestResponseList> getLeaveRequestsByEmployee(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(leaveRequestService.getByEmployee(request));
    }

    @Operation(summary = "Get leave requests by status")
    @PostMapping("/getByStatus")
    public ResponseEntity<LeaveRequestResponseList> getLeaveRequestsByStatus(@Valid @RequestBody StatusRequest request) {
        return ResponseEntity.ok(leaveRequestService.getByStatus(request));
    }

    @Operation(summary = "Get cancelled leave requests")
    @PostMapping("/getCancelled")
    public ResponseEntity<LeaveRequestResponseList> getCancelledLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getCancelled());
    }

    @Operation(summary = "Get leave requests by approver ID")
    @PostMapping("/getByApprover")
    public ResponseEntity<LeaveRequestResponseList> getLeaveRequestsByApprover(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(leaveRequestService.getByApprover(request));
    }

    @Operation(summary = "Get leave requests by employee and status")
    @PostMapping("/getByEmployeeAndStatus")
    public ResponseEntity<LeaveRequestResponseList> getLeaveRequestsByEmployeeAndStatus(@Valid @RequestBody EmployeeStatusRequest request) {
        return ResponseEntity.ok(leaveRequestService.getByEmployeeAndStatus(request));
    }

    @Operation(summary = "Get leave requests by date range")
    @PostMapping("/getByDateRange")
    public ResponseEntity<LeaveRequestResponseList> getLeaveRequestsByDateRange(@Valid @RequestBody EmployeeDateRangeRequest request) {
        return ResponseEntity.ok(leaveRequestService.getByDateRange(request));
    }

    @Operation(summary = "Get leave requests by employee, leave type and date range")
    @PostMapping("/getByEmployeeLeaveTypeAndDateRange")
    public ResponseEntity<LeaveRequestResponseList> getLeaveRequestsByEmployeeLeaveTypeAndDateRange(@Valid @RequestBody EmployeeLeaveTypeDateRangeRequest request) {
        return ResponseEntity.ok(leaveRequestService.getByEmployeeLeaveTypeAndDateRange(request));
    }

    @Operation(summary = "Get overlapping leave requests")
    @PostMapping("/getOverlapping")
    public ResponseEntity<LeaveRequestResponseList> getOverlappingLeaveRequests(@Valid @RequestBody EmployeeDateRangeRequest request) {
        return ResponseEntity.ok(leaveRequestService.getOverlapping(request));
    }
}
