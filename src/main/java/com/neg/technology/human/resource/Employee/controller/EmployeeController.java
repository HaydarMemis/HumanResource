package com.neg.technology.human.resource.Employee.controller;

import com.neg.technology.human.resource.Employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.Employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.DateRequest;
import com.neg.technology.human.resource.Employee.model.response.EmployeeListResponse;
import com.neg.technology.human.resource.Employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.Employee.model.mapper.EmployeeMapper;
import com.neg.technology.human.resource.Employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees", description = "Retrieve a list of all employees")
    @ApiResponse(responseCode = "200", description = "Employees list retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<EmployeeListResponse> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.findAll()
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }

    @Operation(summary = "Get employee by ID", description = "Retrieve an employee by their unique ID")
    @ApiResponse(responseCode = "200", description = "Employee found")
    @PostMapping("/getById")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@Valid @RequestBody IdRequest request) {
        return employeeService.findById(request.getId())
                .map(employee -> ResponseEntity.ok(EmployeeMapper.toDTO(employee)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new employee", description = "Create a new employee record")
    @ApiResponse(responseCode = "200", description = "Employee created successfully")
    @PostMapping("/create")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return ResponseEntity.ok(EmployeeMapper.toDTO(employeeService.createEmployee(request)));
    }

    @Operation(summary = "Update employee", description = "Update an existing employee record")
    @ApiResponse(responseCode = "200", description = "Employee updated successfully")
    @PostMapping("/update")
    public ResponseEntity<EmployeeResponse> updateEmployee(@Valid @RequestBody UpdateEmployeeRequest request) {
        if (!employeeService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(EmployeeMapper.toDTO(employeeService.updateEmployee(request)));
    }

    @Operation(summary = "Delete employee", description = "Delete an employee by ID")
    @ApiResponse(responseCode = "204", description = "Employee deleted successfully")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteEmployee(@Valid @RequestBody IdRequest request) {
        if (!employeeService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get active employees", description = "Retrieve all active employees")
    @ApiResponse(responseCode = "200", description = "Active employees retrieved successfully")
    @PostMapping("/getActive")
    public ResponseEntity<EmployeeListResponse> getActiveEmployees() {
        List<EmployeeResponse> employees = employeeService.findByIsActiveTrue()
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }

    @Operation(summary = "Get inactive employees", description = "Retrieve all inactive employees")
    @ApiResponse(responseCode = "200", description = "Inactive employees retrieved successfully")
    @PostMapping("/getInactive")
    public ResponseEntity<EmployeeListResponse> getInactiveEmployees() {
        List<EmployeeResponse> employees = employeeService.findByIsActiveFalse()
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }

    @Operation(summary = "Get employees by department ID", description = "Retrieve all employees in a department")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByDepartment")
    public ResponseEntity<EmployeeListResponse> getEmployeesByDepartment(@Valid @RequestBody IdRequest request) {
        List<EmployeeResponse> employees = employeeService.findByDepartmentId(request.getId())
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }

    @Operation(summary = "Get employees by position ID", description = "Retrieve all employees in a position")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByPosition")
    public ResponseEntity<EmployeeListResponse> getEmployeesByPosition(@Valid @RequestBody IdRequest request) {
        List<EmployeeResponse> employees = employeeService.findByPositionId(request.getId())
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }

    @Operation(summary = "Get employees by company ID", description = "Retrieve all employees in a company")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByCompany")
    public ResponseEntity<EmployeeListResponse> getEmployeesByCompany(@Valid @RequestBody IdRequest request) {
        List<EmployeeResponse> employees = employeeService.findByCompanyId(request.getId())
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }

    @Operation(summary = "Get employees hired before a specific date", description = "Retrieve all employees hired before given date")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getHiredBefore")
    public ResponseEntity<EmployeeListResponse> getEmployeesHiredBefore(@Valid @RequestBody DateRequest request) {
        LocalDateTime date = LocalDateTime.parse(request.getDate());
        List<EmployeeResponse> employees = employeeService.findByHireDateBefore(date)
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }

    @Operation(summary = "Get employees whose employment ended before a specific date", description = "Retrieve employees with employment ended before given date")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getEmploymentEndedBefore")
    public ResponseEntity<EmployeeListResponse> getEmployeesEmploymentEndedBefore(@Valid @RequestBody DateRequest request) {
        LocalDateTime date = LocalDateTime.parse(request.getDate());
        List<EmployeeResponse> employees = employeeService.findByEmploymentEndDateBefore(date)
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(new EmployeeListResponse(employees));
    }
}
