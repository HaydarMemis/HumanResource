package com.neg.technology.human.resource.leave.controller;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.leave.validator.LeaveBalanceValidator;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.leave.model.request.LeaveTypeYearRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Leave Balance Controller", description = "Operations related to leave balance management")
@RestController
@RequestMapping("/api/leave_balances")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;
    private final LeaveBalanceValidator leaveBalanceValidator;

    @Operation(summary = "Get all leave balances")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<LeaveBalanceResponseList>> getAllLeaveBalances() {
        return leaveBalanceService.getAll()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave balance by ID")
    @ApiResponse(responseCode = "200", description = "Leave balance found")
    @ApiResponse(responseCode = "404", description = "Leave balance not found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<LeaveBalanceResponse>> getLeaveBalanceById(@Valid @RequestBody IdRequest request) {
        return leaveBalanceService.getById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Create leave balance")
    @PostMapping("/create")
    public Mono<ResponseEntity<LeaveBalanceResponse>> createLeaveBalance(@Valid @RequestBody CreateLeaveBalanceRequest dto) {
        leaveBalanceValidator.validateCreateDTO(dto);
        return leaveBalanceService.create(dto)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update leave balance")
    @PostMapping("/update")
    public Mono<ResponseEntity<LeaveBalanceResponse>> updateLeaveBalance(@Valid @RequestBody UpdateLeaveBalanceRequest dto) {
        leaveBalanceValidator.validateUpdateDTO(dto);
        return leaveBalanceService.update(dto)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete leave balance")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deleteLeaveBalance(@Valid @RequestBody IdRequest request) {
        return leaveBalanceService.delete(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get leave balances by employee ID")
    @PostMapping("/getByEmployee")
    public Mono<ResponseEntity<LeaveBalanceResponseList>> getLeaveBalancesByEmployee(@Valid @RequestBody IdRequest request) {
        return leaveBalanceService.getByEmployee(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave balances by employee and year")
    @PostMapping("/getByEmployeeAndYear")
    public Mono<ResponseEntity<LeaveBalanceResponseList>> getLeaveBalancesByEmployeeAndYear(@Valid @RequestBody EmployeeYearRequest request) {
        return leaveBalanceService.getByEmployeeAndYear(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave balance by employee and leave type")
    @PostMapping("/getByEmployeeAndLeaveType")
    public Mono<ResponseEntity<LeaveBalanceResponse>> getLeaveBalanceByEmployeeAndLeaveType(@Valid @RequestBody EmployeeLeaveTypeRequest request) {
        return leaveBalanceService.getByEmployeeAndLeaveType(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave balance by employee, leave type, and year")
    @PostMapping("/getByEmployeeLeaveTypeAndYear")
    public Mono<ResponseEntity<LeaveBalanceResponse>> getLeaveBalanceByEmployeeLeaveTypeAndYear(@Valid @RequestBody EmployeeLeaveTypeYearRequest request) {
        return leaveBalanceService.getByEmployeeLeaveTypeAndYear(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave balances by leave type and year")
    @PostMapping("/getByLeaveTypeAndYear")
    public Mono<ResponseEntity<LeaveBalanceResponseList>> getLeaveBalancesByLeaveTypeAndYear(@Valid @RequestBody LeaveTypeYearRequest request) {
        return leaveBalanceService.getByLeaveTypeAndYear(request)
                .map(ResponseEntity::ok);
    }
}