package com.neg.technology.human.resource.employee_project.controller;

import com.neg.technology.human.resource.employee_project.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee_project.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee_project.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.employee_project.model.response.EmployeeProjectResponseList;
import com.neg.technology.human.resource.employee_project.service.EmployeeProjectService;
import com.neg.technology.human.resource.employee_project.validator.EmployeeProjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<EmployeeProjectResponse> create(@Valid @RequestBody CreateEmployeeProjectRequest request) {
        employeeProjectValidator.validateCreateDTO(request);
        return ResponseEntity.ok(employeeProjectService.createEmployeeProject(request));
    }

    @Operation(summary = "Update an existing employee project")
    @ApiResponse(responseCode = "200", description = "Employee project updated successfully")
    @PostMapping("/update")
    public ResponseEntity<EmployeeProjectResponse> update(@Valid @RequestBody UpdateEmployeeProjectRequest request) {
        return employeeProjectService.updateEmployeeProject(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete employee project by ID")
    @ApiResponse(responseCode = "204", description = "Employee project deleted successfully")
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteEmployeeProject(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all employee projects")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<EmployeeProjectResponseList> getAll() {
        List<EmployeeProjectResponse> list = employeeProjectService.getAllEmployeeProjects();
        return ResponseEntity.ok(new EmployeeProjectResponseList(list));
    }

    @Operation(summary = "Get employee project by ID")
    @ApiResponse(responseCode = "200", description = "Employee project found")
    @PostMapping("/getById")
    public ResponseEntity<EmployeeProjectResponse> getById(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.getEmployeeProjectById(request.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get employee projects by employee ID")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getByEmployee")
    public ResponseEntity<EmployeeProjectResponseList> getByEmployee(@Valid @RequestBody IdRequest request) {
        List<EmployeeProjectResponse> list = employeeProjectService.getByEmployeeId(request.getId());
        return ResponseEntity.ok(new EmployeeProjectResponseList(list));
    }

    @Operation(summary = "Get employee projects by project ID")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getByProject")
    public ResponseEntity<EmployeeProjectResponseList> getByProject(@Valid @RequestBody IdRequest request) {
        List<EmployeeProjectResponse> list = employeeProjectService.getByProjectId(request.getId());
        return ResponseEntity.ok(new EmployeeProjectResponseList(list));
    }

    @Operation(summary = "Check if an employee is assigned to a project")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByEmployeeAndProject")
    public ResponseEntity<Boolean> existsByEmployeeAndProject(@RequestParam Long employeeId,
                                                              @RequestParam Long projectId) {
        return ResponseEntity.ok(employeeProjectService.existsByEmployeeIdAndProjectId(employeeId, projectId));
    }
}
