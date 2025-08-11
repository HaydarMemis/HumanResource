package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.utilities.IdRequest;
import com.neg.hr.human.resource.dto.create.CreateEmployeeProjectRequestDTO;
import com.neg.hr.human.resource.dto.entity.EmployeeProjectEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateEmployeeProjectRequestDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.EmployeeProject;
import com.neg.hr.human.resource.entity.Project;
import com.neg.hr.human.resource.mapper.EmployeeProjectMapper;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.ProjectRepository;
import com.neg.hr.human.resource.service.EmployeeProjectService;
import com.neg.hr.human.resource.validator.EmployeeProjectValidator;
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
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeProjectValidator employeeProjectValidator;

    public EmployeeProjectController(
            EmployeeProjectService employeeProjectService,
            EmployeeRepository employeeRepository,
            ProjectRepository projectRepository,
            EmployeeProjectValidator employeeProjectValidator) {
        this.employeeProjectService = employeeProjectService;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.employeeProjectValidator = employeeProjectValidator;
    }

    @Operation(summary = "Get all employee projects", description = "Retrieve all employee project records")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<EmployeeProjectEntityDTO>> getAll() {
        List<EmployeeProjectEntityDTO> projects = employeeProjectService.findAll()
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Get employee project by ID", description = "Retrieve an employee project by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee project found"),
            @ApiResponse(responseCode = "404", description = "Employee project not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<EmployeeProjectEntityDTO> getById(
            @Parameter(description = "ID of the employee project", required = true)
            @Valid @RequestBody IdRequest request) {
        return employeeProjectService.findById(request.getId())
                .map(EmployeeProjectMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new employee project", description = "Create a new employee project record")
    @ApiResponse(responseCode = "200", description = "Employee project created successfully")
    @PostMapping("/create")
    public ResponseEntity<EmployeeProjectEntityDTO> create(
            @Parameter(description = "Employee project creation data", required = true)
            @Valid @RequestBody CreateEmployeeProjectRequestDTO dto) {

        employeeProjectValidator.validateCreateDTO(dto);

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        EmployeeProject entity = EmployeeProjectMapper.toEntity(dto, employee, project);
        EmployeeProject saved = employeeProjectService.save(entity);

        return ResponseEntity.ok(EmployeeProjectMapper.toDTO(saved));
    }

    @Operation(summary = "Update existing employee project", description = "Update an existing employee project record")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee project updated successfully"),
            @ApiResponse(responseCode = "404", description = "Employee project not found")
    })
    @PostMapping("/update")
    public ResponseEntity<EmployeeProjectEntityDTO> update(
            @Parameter(description = "Employee project update data", required = true)
            @Valid @RequestBody UpdateEmployeeProjectRequestDTO dto) {

        employeeProjectValidator.validateUpdateDTO(dto.getId(), dto);

        Employee employee = null;
        if (dto.getEmployeeId() != null) {
            employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        }

        Project project = null;
        if (dto.getProjectId() != null) {
            project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));
        }

        EmployeeProject existing = employeeProjectService.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("EmployeeProject not found with ID: " + dto.getId()));

        EmployeeProjectMapper.updateEntity(existing, dto, employee, project);

        EmployeeProject updated = employeeProjectService.update(dto.getId(), existing);

        return ResponseEntity.ok(EmployeeProjectMapper.toDTO(updated));
    }

    @Operation(summary = "Delete employee project by ID", description = "Delete an employee project by its ID")
    @ApiResponse(responseCode = "204", description = "Employee project deleted successfully")
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the employee project to delete", required = true)
            @Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete employee projects by employee ID", description = "Delete all projects of an employee")
    @ApiResponse(responseCode = "204", description = "Employee projects deleted successfully")
    @PostMapping("/deleteByEmployee")
    public ResponseEntity<Void> deleteByEmployee(
            @Parameter(description = "Employee ID whose projects to delete", required = true)
            @Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteByEmployeeId(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete employee projects by project ID", description = "Delete all employee assignments of a project")
    @ApiResponse(responseCode = "204", description = "Employee projects deleted successfully")
    @PostMapping("/deleteByProject")
    public ResponseEntity<Void> deleteByProject(
            @Parameter(description = "Project ID whose employee assignments to delete", required = true)
            @Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteByProjectId(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get employee projects by employee ID", description = "Retrieve projects assigned to an employee")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getByEmployee")
    public ResponseEntity<List<EmployeeProjectEntityDTO>> getByEmployee(
            @Parameter(description = "Employee ID to get projects for", required = true)
            @Valid @RequestBody IdRequest request) {
        List<EmployeeProjectEntityDTO> projects = employeeProjectService.findByEmployeeId(request.getId())
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Get employee projects by project ID", description = "Retrieve employees assigned to a project")
    @ApiResponse(responseCode = "200", description = "List of employee projects retrieved successfully")
    @PostMapping("/getByProject")
    public ResponseEntity<List<EmployeeProjectEntityDTO>> getByProject(
            @Parameter(description = "Project ID to get employee assignments for", required = true)
            @Valid @RequestBody IdRequest request) {
        List<EmployeeProjectEntityDTO> projects = employeeProjectService.findByProjectId(request.getId())
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
        return ResponseEntity.ok(projects);
    }

    @Operation(summary = "Check if an employee is assigned to a project", description = "Returns true if the employee is assigned to the given project")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByEmployeeAndProject")
    public ResponseEntity<Boolean> existsByEmployeeAndProject(
            @Parameter(description = "Employee ID", required = true)
            @RequestParam Long employeeId,
            @Parameter(description = "Project ID", required = true)
            @RequestParam Long projectId) {
        boolean exists = employeeProjectService.existsByEmployeeIdAndProjectId(employeeId, projectId);
        return ResponseEntity.ok(exists);
    }
}
