package com.neg.technology.human.resource.Department.controller;

import com.neg.technology.human.resource.Department.model.entity.Department;
import com.neg.technology.human.resource.Department.model.mapper.DepartmentMapper;
import com.neg.technology.human.resource.Department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.Department.service.DepartmentService;
import com.neg.technology.human.resource.Department.validator.DepartmentValidator;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Department Controller", description = "Operations related to department management")
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentValidator departmentValidator;

    public DepartmentController(DepartmentService departmentService,
                                DepartmentValidator departmentValidator) {
        this.departmentService = departmentService;
        this.departmentValidator = departmentValidator;
    }

    @Operation(summary = "Get all departments", description = "Retrieve a list of all departments")
    @ApiResponse(responseCode = "200", description = "List of departments retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.findAll()
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
        return ResponseEntity.ok(departments);
    }

    @Operation(summary = "Get department by ID", description = "Retrieve a department by its unique ID")
    @ApiResponse(responseCode = "200", description = "Department found")
    @ApiResponse(responseCode = "404", description = "Department not found")
    @PostMapping("/getById")
    public ResponseEntity<DepartmentResponse> getDepartmentById(
            @Parameter(description = "ID of the department to be retrieved", required = true)
            @Valid @RequestBody IdRequest request) {

        return departmentService.findById(request.getId())
                .map(department -> ResponseEntity.ok(DepartmentMapper.toDTO(department)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get department by name", description = "Retrieve a department by its name")
    @ApiResponse(responseCode = "200", description = "Department found")
    @ApiResponse(responseCode = "404", description = "Department not found")
    @PostMapping("/getByName")
    public ResponseEntity<DepartmentResponse> getDepartmentByName(
            @Parameter(description = "Name of the department to be retrieved", required = true)
            @Valid @RequestBody NameRequest request) {

        return departmentService.findByName(request.getName())
                .map(department -> ResponseEntity.ok(DepartmentMapper.toDTO(department)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new department", description = "Create a new department record")
    @ApiResponse(responseCode = "200", description = "Department created successfully")
    @PostMapping("/create")
    public ResponseEntity<DepartmentResponse> createDepartment(
            @Parameter(description = "Department data for creation", required = true)
            @Valid @RequestBody CreateDepartmentRequest request) {

        departmentValidator.validateCreate(request);
        Department department = DepartmentMapper.toEntity(request);
        Department saved = departmentService.save(department);
        return ResponseEntity.ok(DepartmentMapper.toDTO(saved));
    }

    @Operation(summary = "Update existing department", description = "Update details of an existing department")
    @ApiResponse(responseCode = "200", description = "Department updated successfully")
    @ApiResponse(responseCode = "404", description = "Department not found")
    @PostMapping("/update")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @Valid @RequestBody UpdateDepartmentRequest request) {

        if (!departmentService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }

        departmentValidator.validateUpdate(request);
        Department department = DepartmentMapper.toEntity(request);
        Department updated = departmentService.update(request.getId(), department);

        return ResponseEntity.ok(DepartmentMapper.toDTO(updated));
    }

    @Operation(summary = "Delete department", description = "Delete a department by ID")
    @ApiResponse(responseCode = "204", description = "Department deleted successfully")
    @ApiResponse(responseCode = "404", description = "Department not found")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteDepartment(
            @Parameter(description = "ID of the department to delete", required = true)
            @Valid @RequestBody IdRequest request) {

        try {
            departmentService.deleteById(request.getId());
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Check if department exists by name", description = "Returns true if a department with the given name exists")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByName")
    public ResponseEntity<Boolean> existsByName(
            @Parameter(description = "Department name to check", required = true)
            @Valid @RequestBody NameRequest request) {
        boolean exists = departmentService.existsByName(request.getName());
        return ResponseEntity.ok(exists);
    }

    @Operation(summary = "Get departments by location", description = "Retrieve all departments in a specific location")
    @ApiResponse(responseCode = "200", description = "Departments retrieved successfully")
    @PostMapping("/getByLocation")
    public ResponseEntity<List<DepartmentResponse>> getDepartmentsByLocation(
            @Parameter(description = "Exact location of departments to retrieve", required = true)
            @Valid @RequestBody String location) {

        List<DepartmentResponse> departments = departmentService.findByLocation(location)
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
        return ResponseEntity.ok(departments);
    }

    @Operation(summary = "Search departments by location keyword", description = "Retrieve all departments where the location contains the given keyword (case-insensitive)")
    @ApiResponse(responseCode = "200", description = "Departments retrieved successfully")
    @PostMapping("/getByLocationContaining")
    public ResponseEntity<List<DepartmentResponse>> getDepartmentsByLocationContaining(
            @Parameter(description = "Location keyword to search", required = true)
            @Valid @RequestBody String keyword) {

        List<DepartmentResponse> departments = departmentService.findByLocationContainingIgnoreCase(keyword)
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
        return ResponseEntity.ok(departments);
    }
}
