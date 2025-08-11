package com.neg.technology.human.resource.Employee.controller;

import com.neg.technology.human.resource.dto.utilities.DateRequest;
import com.neg.technology.human.resource.dto.utilities.IdRequest;
import com.neg.technology.human.resource.dto.create.CreateEmployeeRequestDTO;
import com.neg.technology.human.resource.dto.entity.EmployeeEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateEmployeeRequestDTO;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Employee.model.mapper.EmployeeMapper;
import com.neg.technology.human.resource.Employee.service.EmployeeService;
import com.neg.technology.human.resource.Employee.validator.EmployeeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Employee Controller", description = "Operations related to employee management")
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeValidator employeeValidator;

    public EmployeeController(EmployeeService employeeService,
                              EmployeeValidator employeeValidator) {
        this.employeeService = employeeService;
        this.employeeValidator = employeeValidator;
    }

    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees")
    @ApiResponse(responseCode = "200", description = "List of employees retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<EmployeeEntityDTO>> getAllEmployees() {
        List<EmployeeEntityDTO> employees = employeeService.findAll()
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employee by ID", description = "Retrieve an employee by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<EmployeeEntityDTO> getEmployeeById(
            @Parameter(description = "ID of the employee to retrieve", required = true)
            @Valid @RequestBody IdRequest request) {
        return employeeService.findById(request.getId())
                .map(emp -> ResponseEntity.ok(EmployeeMapper.toDTO(emp)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new employee", description = "Create a new employee record")
    @ApiResponse(responseCode = "200", description = "Employee created successfully")
    @PostMapping("/create")
    public ResponseEntity<EmployeeEntityDTO> createEmployee(
            @Parameter(description = "Employee data for creation", required = true)
            @Valid @RequestBody CreateEmployeeRequestDTO dto) {
        employeeValidator.validateCreateDTO(dto);
        Employee saved = employeeService.createEmployee(dto);
        return ResponseEntity.ok(EmployeeMapper.toDTO(saved));
    }

    @Operation(summary = "Update existing employee", description = "Update details of an existing employee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PostMapping("/update")
    public ResponseEntity<EmployeeEntityDTO> updateEmployee(
            @Valid @RequestBody UpdateEmployeeRequestDTO dto) {
        if (!employeeService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        employeeValidator.validateUpdateDTO(dto);
        Employee updated = employeeService.updateEmployee(dto);
        return ResponseEntity.ok(EmployeeMapper.toDTO(updated));
    }

    @Operation(summary = "Delete employee", description = "Delete an employee by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "ID of the employee to delete", required = true)
            @Valid @RequestBody IdRequest request) {
        if (!employeeService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get active employees", description = "Retrieve all employees that are currently active")
    @ApiResponse(responseCode = "200", description = "Active employees retrieved successfully")
    @PostMapping("/getActive")
    public ResponseEntity<List<EmployeeEntityDTO>> getActiveEmployees() {
        List<EmployeeEntityDTO> employees = employeeService.findByIsActiveTrue()
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get inactive employees", description = "Retrieve all employees that are currently inactive")
    @ApiResponse(responseCode = "200", description = "Inactive employees retrieved successfully")
    @PostMapping("/getInactive")
    public ResponseEntity<List<EmployeeEntityDTO>> getInactiveEmployees() {
        List<EmployeeEntityDTO> employees = employeeService.findByIsActiveFalse()
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees by department ID", description = "Retrieve all employees belonging to a specific department")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByDepartment")
    public ResponseEntity<List<EmployeeEntityDTO>> getEmployeesByDepartment(
            @Parameter(description = "Department ID", required = true)
            @Valid @RequestBody IdRequest request) {
        List<EmployeeEntityDTO> employees = employeeService.findByDepartmentId(request.getId())
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees by position ID", description = "Retrieve all employees holding a specific position")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByPosition")
    public ResponseEntity<List<EmployeeEntityDTO>> getEmployeesByPosition(
            @Parameter(description = "Position ID", required = true)
            @Valid @RequestBody IdRequest request) {
        List<EmployeeEntityDTO> employees = employeeService.findByPositionId(request.getId())
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees by company ID", description = "Retrieve all employees working for a specific company")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByCompany")
    public ResponseEntity<List<EmployeeEntityDTO>> getEmployeesByCompany(
            @Parameter(description = "Company ID", required = true)
            @Valid @RequestBody IdRequest request) {
        List<EmployeeEntityDTO> employees = employeeService.findByCompanyId(request.getId())
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees hired before a specific date", description = "Retrieve all employees hired before the given date")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getHiredBefore")
    public ResponseEntity<List<EmployeeEntityDTO>> getEmployeesHiredBefore(
            @Parameter(description = "Date in ISO format (e.g., 2024-01-01T00:00:00)", required = true)
            @Valid @RequestBody DateRequest request) {
        LocalDateTime dateTime = LocalDateTime.parse(request.getDate());
        List<EmployeeEntityDTO> employees = employeeService.findByHireDateBefore(dateTime)
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Get employees whose employment ended before a specific date", description = "Retrieve all employees whose employment end date is before the given date")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getEmploymentEndedBefore")
    public ResponseEntity<List<EmployeeEntityDTO>> getEmployeesEmploymentEndedBefore(
            @Parameter(description = "Date in ISO format (e.g., 2024-01-01T00:00:00)", required = true)
            @Valid @RequestBody DateRequest request) {
        LocalDateTime dateTime = LocalDateTime.parse(request.getDate());
        List<EmployeeEntityDTO> employees = employeeService.findByEmploymentEndDateBefore(dateTime)
                .stream()
                .map(EmployeeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(employees);
    }
}
