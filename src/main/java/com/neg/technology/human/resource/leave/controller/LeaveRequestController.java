package com.neg.technology.human.resource.leave.controller;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.response.ApprovedLeaveDatesResponseList;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.leave.service.LeaveRequestService;
import com.neg.technology.human.resource.leave.validator.LeaveRequestValidator;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.StatusRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeDateRangeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "LeaveRequest Controller", description = "Operations related to leave requests management")
@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final LeaveRequestValidator leaveRequestValidator;

    @Operation(summary = "Get all leave requests")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getAllLeaveRequests() {
        return leaveRequestService.getAll()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave request by ID")
    @ApiResponse(responseCode = "200", description = "Leave request found")
    @ApiResponse(responseCode = "404", description = "Leave request not found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<LeaveRequestResponse>> getLeaveRequestById(@Valid @RequestBody IdRequest request) {
        return leaveRequestService.getById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Create a new leave request")
    @PostMapping("/create")
    public Mono<ResponseEntity<LeaveRequestResponse>> createLeaveRequest(@Valid @RequestBody CreateLeaveRequestRequest dto) {
        return Mono.fromCallable(() -> {
                    leaveRequestValidator.validateCreateDTO(dto);
                    return dto;
                })
                .flatMap(validatedDto -> leaveRequestService.create(validatedDto))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update an existing leave request")
    @PostMapping("/update")
    public Mono<ResponseEntity<LeaveRequestResponse>> updateLeaveRequest(@Valid @RequestBody UpdateLeaveRequestRequest dto) {
        return Mono.fromCallable(() -> {
                    leaveRequestValidator.validateUpdateDTO(dto);
                    return dto;
                })
                .flatMap(validatedDto -> leaveRequestService.update(validatedDto))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete a leave request")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deleteLeaveRequest(@Valid @RequestBody IdRequest request) {
        return leaveRequestService.delete(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get leave requests by employee ID")
    @PostMapping("/getByEmployee")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getLeaveRequestsByEmployee(@Valid @RequestBody IdRequest request) {
        return leaveRequestService.getByEmployee(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave requests by status")
    @PostMapping("/getByStatus")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getLeaveRequestsByStatus(@Valid @RequestBody StatusRequest request) {
        return leaveRequestService.getByStatus(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get cancelled leave requests")
    @PostMapping("/getCancelled")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getCancelledLeaveRequests() {
        return leaveRequestService.getCancelled()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave requests by approver ID")
    @PostMapping("/getByApprover")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getLeaveRequestsByApprover(@Valid @RequestBody IdRequest request) {
        return leaveRequestService.getByApprover(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave requests by employee and status")
    @PostMapping("/getByEmployeeAndStatus")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getLeaveRequestsByEmployeeAndStatus(@Valid @RequestBody EmployeeStatusRequest request) {
        return leaveRequestService.getByEmployeeAndStatus(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave requests by date range")
    @PostMapping("/getByDateRange")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getLeaveRequestsByDateRange(@Valid @RequestBody EmployeeDateRangeRequest request) {
        return leaveRequestService.getByDateRange(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave requests by employee, leave type and date range")
    @PostMapping("/getByEmployeeLeaveTypeAndDateRange")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getLeaveRequestsByEmployeeLeaveTypeAndDateRange(@Valid @RequestBody EmployeeLeaveTypeDateRangeRequest request) {
        return leaveRequestService.getByEmployeeLeaveTypeAndDateRange(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get overlapping leave requests")
    @PostMapping("/getOverlapping")
    public Mono<ResponseEntity<LeaveRequestResponseList>> getOverlappingLeaveRequests(@Valid @RequestBody EmployeeDateRangeRequest request) {
        return leaveRequestService.getOverlapping(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get approved leave dates by employee ID")
    @PostMapping("/getApprovedByEmployee")
    public Mono<ResponseEntity<ApprovedLeaveDatesResponseList>> getApprovedLeaveRequestsByEmployee(
            @Valid @RequestBody IdRequest request) {
        return leaveRequestService.getApprovedByEmployee(request)
                .map(ResponseEntity::ok);
    }

}