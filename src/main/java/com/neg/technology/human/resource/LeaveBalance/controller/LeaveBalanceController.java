package com.neg.technology.human.resource.LeaveBalance.controller;

import com.neg.technology.human.resource.Employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.LeaveType.model.request.LeaveTypeYearRequest;
import com.neg.technology.human.resource.LeaveBalance.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.LeaveBalance.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.LeaveBalance.model.entity.LeaveBalance;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import com.neg.technology.human.resource.LeaveBalance.model.mapper.LeaveBalanceMapper;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.LeaveType.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.LeaveBalance.service.LeaveBalanceService;
import com.neg.technology.human.resource.LeaveBalance.validator.LeaveBalanceValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final LeaveBalanceValidator leaveBalanceValidator;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveBalanceController(
            LeaveBalanceService leaveBalanceService,
            LeaveBalanceValidator leaveBalanceValidator,
            LeaveTypeRepository leaveTypeRepository,
            EmployeeRepository employeeRepository) {
        this.leaveBalanceService = leaveBalanceService;
        this.leaveBalanceValidator = leaveBalanceValidator;
        this.leaveTypeRepository = leaveTypeRepository;
        this.employeeRepository = employeeRepository;
    }

    @Operation(summary = "Get all leave balances", description = "Retrieve all leave balance records")
    @ApiResponse(responseCode = "200", description = "List of leave balances retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<LeaveBalanceResponse>> getAllLeaveBalances() {
        List<LeaveBalanceResponse> list = leaveBalanceService.findAll()
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave balance by ID", description = "Retrieve leave balance by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave balance found"),
            @ApiResponse(responseCode = "404", description = "Leave balance not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalanceById(
            @Parameter(description = "ID of the leave balance", required = true)
            @Valid @RequestBody IdRequest request) {
        return leaveBalanceService.findById(request.getId())
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create leave balance", description = "Create a new leave balance record")
    @ApiResponse(responseCode = "200", description = "Leave balance created successfully")
    @PostMapping("/create")
    public ResponseEntity<LeaveBalanceResponse> createLeaveBalance(
            @Parameter(description = "Leave balance creation data", required = true)
            @Valid @RequestBody CreateLeaveBalanceRequest dto) {
        leaveBalanceValidator.validateCreateDTO(dto);

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type ID"));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        LeaveBalance entity = LeaveBalanceMapper.toEntity(dto, employee, leaveType);
        LeaveBalance saved = leaveBalanceService.save(entity);

        return ResponseEntity.ok(LeaveBalanceMapper.toDTO(saved));
    }

    @Operation(summary = "Update leave balance", description = "Update an existing leave balance record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave balance updated successfully"),
            @ApiResponse(responseCode = "404", description = "Leave balance not found")
    })
    @PostMapping("/update")
    public ResponseEntity<LeaveBalanceResponse> updateLeaveBalance(
            @Parameter(description = "Leave balance update data", required = true)
            @Valid @RequestBody UpdateLeaveBalanceRequest dto) {

        if (!leaveBalanceService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }

        leaveBalanceValidator.validateUpdateDTO(dto);

        LeaveBalance leaveBalance = leaveBalanceService.findById(dto.getId()).get();

        Employee employee = (dto.getEmployeeId() != null)
                ? employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"))
                : null;

        LeaveType leaveType = (dto.getLeaveTypeId() != null)
                ? leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type ID"))
                : null;

        LeaveBalanceMapper.updateEntity(leaveBalance, dto, employee, leaveType);
        LeaveBalance updated = leaveBalanceService.save(leaveBalance);

        return ResponseEntity.ok(LeaveBalanceMapper.toDTO(updated));
    }

    @Operation(summary = "Delete leave balance", description = "Delete a leave balance by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Leave balance deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Leave balance not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveBalance(
            @Parameter(description = "ID of the leave balance to delete", required = true)
            @Valid @RequestBody IdRequest request) {
        if (!leaveBalanceService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        leaveBalanceService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get leave balances by employee ID", description = "Retrieve leave balances for an employee")
    @ApiResponse(responseCode = "200", description = "List of leave balances retrieved successfully")
    @PostMapping("/getByEmployee")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveBalancesByEmployee(
            @Parameter(description = "Employee ID", required = true)
            @Valid @RequestBody IdRequest request) {
        List<LeaveBalanceResponse> list = leaveBalanceService.findByEmployeeId(request.getId())
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave balances by employee and year", description = "Retrieve leave balances for an employee in a specific year")
    @ApiResponse(responseCode = "200", description = "List of leave balances retrieved successfully")
    @PostMapping("/getByEmployeeAndYear")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveBalancesByEmployeeAndYear(
            @Parameter(description = "Employee and year data", required = true)
            @Valid @RequestBody EmployeeYearRequest request) {
        List<LeaveBalanceResponse> list = leaveBalanceService.findByEmployeeIdAndDate(request.getYear(), request.getEmployeeId())
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave balance by employee and leave type", description = "Retrieve leave balance for an employee by leave type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave balance found"),
            @ApiResponse(responseCode = "404", description = "Leave balance not found")
    })
    @PostMapping("/getByEmployeeAndLeaveType")
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalanceByEmployeeAndLeaveType(
            @Parameter(description = "Employee and leave type data", required = true)
            @Valid @RequestBody EmployeeLeaveTypeRequest request) {
        return leaveBalanceService.findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get leave balance by employee, leave type, and year", description = "Retrieve leave balance for an employee by leave type and year")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave balance found"),
            @ApiResponse(responseCode = "404", description = "Leave balance not found")
    })
    @PostMapping("/getByEmployeeLeaveTypeAndYear")
    public ResponseEntity<LeaveBalanceResponse> getLeaveBalanceByEmployeeLeaveTypeAndYear(
            @Parameter(description = "Employee, leave type and year data", required = true)
            @Valid @RequestBody EmployeeLeaveTypeYearRequest request) {
        return leaveBalanceService.findByEmployeeIdAndLeaveTypeIdAndDate(
                        request.getEmployeeId(),
                        request.getLeaveTypeId(),
                        request.getYear())
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get leave balances by leave type and year", description = "Retrieve leave balances for a leave type in a specific year")
    @ApiResponse(responseCode = "200", description = "List of leave balances retrieved successfully")
    @PostMapping("/getByLeaveTypeAndYear")
    public ResponseEntity<List<LeaveBalanceResponse>> getLeaveBalancesByLeaveTypeAndYear(
            @Parameter(description = "Leave type and year data", required = true)
            @Valid @RequestBody LeaveTypeYearRequest request) {
        List<LeaveBalanceResponse> list = leaveBalanceService.findByLeaveTypeIdAndDate(request.getLeaveTypeId(), request.getYear())
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }
}
