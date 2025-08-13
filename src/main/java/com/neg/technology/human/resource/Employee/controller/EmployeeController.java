package com.neg.technology.human.resource.Employee.controller;

import com.neg.technology.human.resource.Employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.Employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.DateRequest;
import com.neg.technology.human.resource.Employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.Employee.model.response.EmployeeListResponse;
import com.neg.technology.human.resource.Employee.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "Create new employee")
    @ApiResponse(responseCode = "200", description = "Employee created successfully")
    @PostMapping("/create")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    @Operation(summary = "Update existing employee")
    @ApiResponse(responseCode = "200", description = "Employee updated successfully")
    @PostMapping("/update")
    public ResponseEntity<EmployeeResponse> updateEmployee(@Valid @RequestBody UpdateEmployeeRequest request) {
        return employeeService.updateEmployee(request);
    }

    @Operation(summary = "Get employee by ID")
    @ApiResponse(responseCode = "200", description = "Employee found")
    @PostMapping("/getById")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@Valid @RequestBody IdRequest request) {
        return employeeService.getEmployeeById(request);
    }

    @Operation(summary = "Get all employees")
    @ApiResponse(responseCode = "200", description = "Employees list retrieved successfully")
    @GetMapping("/getAll")
    public ResponseEntity<EmployeeListResponse> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @Operation(summary = "Delete employee by ID")
    @ApiResponse(responseCode = "204", description = "Employee deleted successfully")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteEmployee(@Valid @RequestBody IdRequest request) {
        return employeeService.deleteEmployee(request);
    }

    @Operation(summary = "Get all active employees")
    @ApiResponse(responseCode = "200", description = "Active employees retrieved successfully")
    @PostMapping("/getActive")
    public ResponseEntity<EmployeeListResponse> getActiveEmployees() {
        return employeeService.getActiveEmployees();
    }

    @Operation(summary = "Get all inactive employees")
    @ApiResponse(responseCode = "200", description = "Inactive employees retrieved successfully")
    @PostMapping("/getInactive")
    public ResponseEntity<EmployeeListResponse> getInactiveEmployees() {
        return employeeService.getInactiveEmployees();
    }

    @Operation(summary = "Get employees by department ID")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByDepartment")
    public ResponseEntity<EmployeeListResponse> getEmployeesByDepartment(@Valid @RequestBody IdRequest request) {
        return employeeService.getEmployeesByDepartment(request);
    }

    @Operation(summary = "Get employees by position ID")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByPosition")
    public ResponseEntity<EmployeeListResponse> getEmployeesByPosition(@Valid @RequestBody IdRequest request) {
        return employeeService.getEmployeesByPosition(request);
    }

    @Operation(summary = "Get employees by company ID")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByCompany")
    public ResponseEntity<EmployeeListResponse> getEmployeesByCompany(@Valid @RequestBody IdRequest request) {
        return employeeService.getEmployeesByCompany(request);
    }

    @Operation(summary = "Get employees hired before a specific date")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getHiredBefore")
    public ResponseEntity<EmployeeListResponse> getEmployeesHiredBefore(@Valid @RequestBody DateRequest request) {
        return employeeService.getEmployeesHiredBefore(request);
    }

    @Operation(summary = "Get employees whose employment ended before a specific date")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getEmploymentEndedBefore")
    public ResponseEntity<EmployeeListResponse> getEmployeesEmploymentEndedBefore(@Valid @RequestBody DateRequest request) {
        return employeeService.getEmployeesEmploymentEndedBefore(request);
    }
}
