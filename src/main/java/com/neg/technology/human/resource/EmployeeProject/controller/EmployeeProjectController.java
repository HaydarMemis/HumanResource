package com.neg.technology.human.resource.EmployeeProject.controller;

import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.EmployeeProject.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.EmployeeProject.service.EmployeeProjectService;
import com.neg.technology.human.resource.EmployeeProject.validator.EmployeeProjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Employee Project Controller", description = "Operations related to employee projects management")
@RestController
@RequestMapping("/api/employee-projects")
public class EmployeeProjectController {

    private final EmployeeProjectService employeeProjectService;
    private final EmployeeProjectValidator employeeProjectValidator;

    public EmployeeProjectController(EmployeeProjectService employeeProjectService,
                                     EmployeeProjectValidator employeeProjectValidator) {
        this.employeeProjectService = employeeProjectService;
        this.employeeProjectValidator = employeeProjectValidator;
    }

    @Operation(summary = "Get all employee projects")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<EmployeeProjectResponse>> getAll() {
        return ResponseEntity.ok(employeeProjectService.getAllEmployeeProjects());
    }

    @Operation(summary = "Get employee project by ID")
    @ApiResponse(responseCode = "200", description = "Employee project found")
    @ApiResponse(responseCode = "404", description = "Employee project not found")
    @PostMapping("/getById")
    public ResponseEntity<EmployeeProjectResponse> getById(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.getEmployeeProjectById(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new employee project")
    @ApiResponse(responseCode = "200", description = "Employee project created successfully")
    @PostMapping("/create")
    public ResponseEntity<EmployeeProjectResponse> create(@Valid @RequestBody CreateEmployeeProjectRequest request) {
        employeeProjectValidator.validateCreateDTO(request);
        return ResponseEntity.ok(employeeProjectService.createEmployeeProject(request));
    }

    @Operation(summary = "Update existing employee project")
    @ApiResponse(responseCode = "200", description = "Employee project updated successfully")
    @ApiResponse(responseCode = "404", description = "Employee project not found")
    @PostMapping("/update")
    public ResponseEntity<EmployeeProjectResponse> update(@Valid @RequestBody UpdateEmployeeProjectRequest request) {
        employeeProjectValidator.validateUpdateDTO(request.getId(), request);
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

    @Operation(summary = "Delete employee projects by employee ID")
    @ApiResponse(responseCode = "204", description = "Employee projects deleted successfully")
    @PostMapping("/deleteByEmployee")
    public ResponseEntity<Void> deleteByEmployee(@Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteByEmployeeId(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete employee projects by project ID")
    @ApiResponse(responseCode = "204", description = "Employee projects deleted successfully")
    @PostMapping("/deleteByProject")
    public ResponseEntity<Void> deleteByProject(@Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteByProjectId(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get employee projects by employee ID")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getByEmployee")
    public ResponseEntity<List<EmployeeProjectResponse>> getByEmployee(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(employeeProjectService.getByEmployeeId(request.getId()));
    }

    @Operation(summary = "Get employee projects by project ID")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getByProject")
    public ResponseEntity<List<EmployeeProjectResponse>> getByProject(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(employeeProjectService.getByProjectId(request.getId()));
    }

    @Operation(summary = "Check if an employee is assigned to a project")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByEmployeeAndProject")
    public ResponseEntity<Boolean> existsByEmployeeAndProject(
            @RequestParam Long employeeId,
            @RequestParam Long projectId) {
        return ResponseEntity.ok(employeeProjectService.existsByEmployeeIdAndProjectId(employeeId, projectId));
    }
}
