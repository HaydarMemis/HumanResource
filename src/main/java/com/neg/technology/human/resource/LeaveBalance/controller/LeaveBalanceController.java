package com.neg.technology.human.resource.LeaveBalance.controller;

import com.neg.technology.human.resource.LeaveBalance.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.LeaveBalance.service.LeaveBalanceService;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.LeaveType.model.request.LeaveTypeYearRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Leave Balance Controller", description = "Operations related to leave balance management")
@RestController
@RequestMapping("/api/leave_balances")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;

    public LeaveBalanceController(LeaveBalanceService leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    @Operation(summary = "Get all leave balances")
    @PostMapping("/getAll")
    public ResponseEntity<List<LeaveBalanceResponse>> getAllLeaveBalances() {
        return ResponseEntity.ok(leaveBalanceService.getAll());
    }

    @Operation(summary = "Get leave balance by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave balance found"),
            @ApiResponse(responseCode = "404", description = "Leave balance not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalanceById(@Valid @RequestBody IdRequest request) {
        return leaveBalanceService.getById(request);
    }

    @Operation(summary = "Create leave balance")
    @PostMapping("/create")
    public ResponseEntity<LeaveBalanceResponse> createLeaveBalance(@Valid @RequestBody CreateLeaveBalanceRequest dto) {
        return ResponseEntity.ok(leaveBalanceService.create(dto));
    }

    @Operation(summary = "Update leave balance")
    @PostMapping("/update")
    public ResponseEntity<LeaveBalanceResponse> updateLeaveBalance(@Valid @RequestBody UpdateLeaveBalanceRequest dto) {
        return leaveBalanceService.update(dto);
    }

    @Operation(summary = "Delete leave balance")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveBalance(@Valid @RequestBody IdRequest request) {
        leaveBalanceService.delete(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get leave balances by employee ID")
    @PostMapping("/getByEmployee")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveBalancesByEmployee(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(leaveBalanceService.getByEmployee(request));
    }

    @Operation(summary = "Get leave balances by employee and year")
    @PostMapping("/getByEmployeeAndYear")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveBalancesByEmployeeAndYear(@Valid @RequestBody EmployeeYearRequest request) {
        return ResponseEntity.ok(leaveBalanceService.getByEmployeeAndYear(request));
    }

    @Operation(summary = "Get leave balance by employee and leave type")
    @PostMapping("/getByEmployeeAndLeaveType")
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalanceByEmployeeAndLeaveType(@Valid @RequestBody EmployeeLeaveTypeRequest request) {
        return leaveBalanceService.getByEmployeeAndLeaveType(request);
    }

    @Operation(summary = "Get leave balance by employee, leave type, and year")
    @PostMapping("/getByEmployeeLeaveTypeAndYear")
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalanceByEmployeeLeaveTypeAndYear(@Valid @RequestBody EmployeeLeaveTypeYearRequest request) {
        return leaveBalanceService.getByEmployeeLeaveTypeAndYear(request);
    }

    @Operation(summary = "Get leave balances by leave type and year")
    @PostMapping("/getByLeaveTypeAndYear")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveBalancesByLeaveTypeAndYear(@Valid @RequestBody LeaveTypeYearRequest request) {
        return ResponseEntity.ok(leaveBalanceService.getByLeaveTypeAndYear(request));
    }
}
