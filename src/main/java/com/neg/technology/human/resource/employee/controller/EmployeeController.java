package com.neg.technology.human.resource.employee.controller;

import com.neg.technology.human.resource.company.model.request.CompanyIdRequest;
import com.neg.technology.human.resource.department.model.request.DepartmentIdRequest;
import com.neg.technology.human.resource.employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.company.model.request.PositionIdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.DateRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.employee.model.response.EmployeeListResponse;
import com.neg.technology.human.resource.employee.service.EmployeeService;
import com.neg.technology.human.resource.employee.validator.EmployeeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Controller", description = "Operations related to employee management")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeValidator employeeValidator;

    @Operation(summary = "Create new employee")
    @ApiResponse(responseCode = "200", description = "Employee created successfully")
    @PostMapping("/create")
    public Mono<ResponseEntity<EmployeeResponse>> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        employeeValidator.validateCreateDTO(request);
        return employeeService.createEmployee(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update existing employee")
    @ApiResponse(responseCode = "200", description = "Employee updated successfully")
    @PostMapping("/update")
    public Mono<ResponseEntity<EmployeeResponse>> updateEmployee(@Valid @RequestBody UpdateEmployeeRequest request) {
        employeeValidator.validateUpdateDTO(request);
        return employeeService.updateEmployee(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employee by ID")
    @ApiResponse(responseCode = "200", description = "Employee found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<EmployeeResponse>> getEmployeeById(@Valid @RequestBody IdRequest request) {
        return employeeService.getEmployeeById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get all employees")
    @ApiResponse(responseCode = "200", description = "Employees list retrieved successfully")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<EmployeeListResponse>> getAllEmployees() {
        return employeeService.getAllEmployees()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete employee by ID")
    @ApiResponse(responseCode = "204", description = "Employee deleted successfully")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deleteEmployee(@Valid @RequestBody IdRequest request) {
        return employeeService.deleteEmployee(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get all active employees")
    @ApiResponse(responseCode = "200", description = "Active employees retrieved successfully")
    @PostMapping("/getActive")
    public Mono<ResponseEntity<EmployeeListResponse>> getActiveEmployees() {
        return employeeService.getActiveEmployees()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get all inactive employees")
    @ApiResponse(responseCode = "200", description = "Inactive employees retrieved successfully")
    @PostMapping("/getInactive")
    public Mono<ResponseEntity<EmployeeListResponse>> getInactiveEmployees() {
        return employeeService.getInactiveEmployees()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employees by department ID")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByDepartment")
    public Mono<ResponseEntity<EmployeeListResponse>> getEmployeesByDepartment(@Valid @RequestBody DepartmentIdRequest request) {
        return employeeService.getEmployeesByDepartment(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employees by position ID")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByPosition")
    public Mono<ResponseEntity<EmployeeListResponse>> getEmployeesByPosition(@Valid @RequestBody PositionIdRequest request) {
        return employeeService.getEmployeesByPosition(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employees by company ID")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getByCompany")
    public Mono<ResponseEntity<EmployeeListResponse>> getEmployeesByCompany(@Valid @RequestBody CompanyIdRequest request) {
        return employeeService.getEmployeesByCompany(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employees hired before a specific date")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getHiredBefore")
    public Mono<ResponseEntity<EmployeeListResponse>> getEmployeesHiredBefore(@Valid @RequestBody DateRequest request) {
        return employeeService.getEmployeesHiredBefore(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employees whose employment ended before a specific date")
    @ApiResponse(responseCode = "200", description = "Employees retrieved successfully")
    @PostMapping("/getEmploymentEndedBefore")
    public Mono<ResponseEntity<EmployeeListResponse>> getEmployeesEmploymentEndedBefore(@Valid @RequestBody DateRequest request) {
        return employeeService.getEmployeesEmploymentEndedBefore(request)
                .map(ResponseEntity::ok);
    }
}