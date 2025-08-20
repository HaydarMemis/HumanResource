package com.neg.technology.human.resource.employee.controller;

import com.neg.technology.human.resource.employee.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.employee.model.response.EmployeeProjectResponseList;
import com.neg.technology.human.resource.employee.service.EmployeeProjectService;
import com.neg.technology.human.resource.employee.validator.EmployeeProjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/employee-projects")
@RequiredArgsConstructor
@Tag(name = "Employee Project Controller", description = "Operations related to employee projects management")
public class EmployeeProjectController {

    private final EmployeeProjectService employeeProjectService;
    private final EmployeeProjectValidator employeeProjectValidator;

    @Operation(summary = "Create a new employee project")
    @ApiResponse(responseCode = "200", description = "Employee project created successfully")
    @PostMapping("/create")
    public Mono<ResponseEntity<EmployeeProjectResponse>> create(@Valid @RequestBody CreateEmployeeProjectRequest request) {
        employeeProjectValidator.validateCreateDTO(request);
        return employeeProjectService.createEmployeeProject(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update an existing employee project")
    @ApiResponse(responseCode = "200", description = "Employee project updated successfully")
    @PostMapping("/update")
    public Mono<ResponseEntity<EmployeeProjectResponse>> update(@Valid @RequestBody UpdateEmployeeProjectRequest request) {
        employeeProjectValidator.validateUpdateDTO(request.getId(), request);
        return employeeProjectService.updateEmployeeProject(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete employee project by ID")
    @ApiResponse(responseCode = "204", description = "Employee project deleted successfully")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> delete(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.deleteEmployeeProject(request.getId())
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get all employee projects")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<EmployeeProjectResponseList>> getAll() {
        return employeeProjectService.getAllEmployeeProjects()
                .map(EmployeeProjectResponseList::new)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employee project by ID")
    @ApiResponse(responseCode = "200", description = "Employee project found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<EmployeeProjectResponse>> getById(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.getEmployeeProjectById(request.getId())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employee projects by employee ID")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getByEmployee")
    public Mono<ResponseEntity<EmployeeProjectResponseList>> getByEmployee(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.getByEmployeeId(request.getId())
                .map(EmployeeProjectResponseList::new)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get employee projects by project ID")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getByProject")
    public Mono<ResponseEntity<EmployeeProjectResponseList>> getByProject(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.getByProjectId(request.getId())
                .map(EmployeeProjectResponseList::new)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Check if an employee is assigned to a project")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByEmployeeAndProject")
    public Mono<ResponseEntity<Boolean>> existsByEmployeeAndProject(@RequestParam Long employeeId,
                                                                    @RequestParam Long projectId) {
        return employeeProjectService.existsByEmployeeIdAndProjectId(employeeId, projectId)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete employee projects by employee ID")
    @ApiResponse(responseCode = "204", description = "Employee projects deleted successfully")
    @PostMapping("/deleteByEmployee")
    public Mono<ResponseEntity<Void>> deleteByEmployee(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.deleteByEmployeeId(request.getId())
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Delete employee projects by project ID")
    @ApiResponse(responseCode = "204", description = "Employee projects deleted successfully")
    @PostMapping("/deleteByProject")
    public Mono<ResponseEntity<Void>> deleteByProject(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.deleteByProjectId(request.getId())
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}