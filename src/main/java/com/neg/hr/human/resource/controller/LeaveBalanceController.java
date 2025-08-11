package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.Employee.EmployeeYearRequest;
import com.neg.hr.human.resource.dto.LeaveType.EmployeeLeaveTypeRequest;
import com.neg.hr.human.resource.dto.LeaveType.EmployeeLeaveTypeYearRequest;
import com.neg.hr.human.resource.dto.LeaveType.LeaveTypeYearRequest;
import com.neg.hr.human.resource.dto.create.CreateLeaveBalanceRequestDTO;
import com.neg.hr.human.resource.dto.entity.LeaveBalanceEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveBalanceRequestDTO;
import com.neg.hr.human.resource.dto.utilities.IdRequest;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveBalance;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveBalanceMapper;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import com.neg.hr.human.resource.service.LeaveBalanceService;
import com.neg.hr.human.resource.validator.LeaveBalanceValidator;
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
    public ResponseEntity<List<LeaveBalanceEntityDTO>> getAllLeaveBalances() {
        List<LeaveBalanceEntityDTO> list = leaveBalanceService.findAll()
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
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceById(
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
    public ResponseEntity<LeaveBalanceEntityDTO> createLeaveBalance(
            @Parameter(description = "Leave balance creation data", required = true)
            @Valid @RequestBody CreateLeaveBalanceRequestDTO dto) {
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
    public ResponseEntity<LeaveBalanceEntityDTO> updateLeaveBalance(
            @Parameter(description = "Leave balance update data", required = true)
            @Valid @RequestBody UpdateLeaveBalanceRequestDTO dto) {

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
    public ResponseEntity<List<LeaveBalanceEntityDTO>> getLeaveBalancesByEmployee(
            @Parameter(description = "Employee ID", required = true)
            @Valid @RequestBody IdRequest request) {
        List<LeaveBalanceEntityDTO> list = leaveBalanceService.findByEmployeeId(request.getId())
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get leave balances by employee and year", description = "Retrieve leave balances for an employee in a specific year")
    @ApiResponse(responseCode = "200", description = "List of leave balances retrieved successfully")
    @PostMapping("/getByEmployeeAndYear")
    public ResponseEntity<List<LeaveBalanceEntityDTO>> getLeaveBalancesByEmployeeAndYear(
            @Parameter(description = "Employee and year data", required = true)
            @Valid @RequestBody EmployeeYearRequest request) {
        List<LeaveBalanceEntityDTO> list = leaveBalanceService.findByEmployeeIdAndDate(request.getYear(), request.getEmployeeId())
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
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceByEmployeeAndLeaveType(
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
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceByEmployeeLeaveTypeAndYear(
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
    public ResponseEntity<List<LeaveBalanceEntityDTO>> getLeaveBalancesByLeaveTypeAndYear(
            @Parameter(description = "Leave type and year data", required = true)
            @Valid @RequestBody LeaveTypeYearRequest request) {
        List<LeaveBalanceEntityDTO> list = leaveBalanceService.findByLeaveTypeIdAndDate(request.getLeaveTypeId(), request.getYear())
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }
}
