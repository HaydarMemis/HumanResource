package com.neg.technology.human.resource.Department.controller;

import com.neg.technology.human.resource.Department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import com.neg.technology.human.resource.Department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.Department.model.response.DepartmentResponseList;
import com.neg.technology.human.resource.Department.service.DepartmentService;
import com.neg.technology.human.resource.Department.validator.DepartmentValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Department Controller", description = "Operations related to department management")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentValidator departmentValidator;

    @Operation(summary = "Create a new department")
    @ApiResponse(responseCode = "200", description = "Department created successfully")
    @PostMapping("/create")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        departmentValidator.validateCreate(request);
        return ResponseEntity.ok(departmentService.createDepartment(request));
    }

    @Operation(summary = "Update an existing department")
    @ApiResponse(responseCode = "200", description = "Department updated successfully")
    @PostMapping("/update")
    public ResponseEntity<DepartmentResponse> updateDepartment(@Valid @RequestBody UpdateDepartmentRequest request) {
        departmentValidator.validateUpdate(request);
        return ResponseEntity.ok(departmentService.updateDepartment(request));
    }

    @Operation(summary = "Delete a department by ID")
    @ApiResponse(responseCode = "204", description = "Department deleted successfully")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteDepartment(@Valid @RequestBody IdRequest request) {
        departmentService.deleteDepartment(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all departments")
    @ApiResponse(responseCode = "200", description = "List of departments retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<DepartmentResponseList> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @Operation(summary = "Get a department by ID")
    @ApiResponse(responseCode = "200", description = "Department found")
    @PostMapping("/getById")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(departmentService.getDepartmentById(request));
    }

    @Operation(summary = "Get a department by name")
    @ApiResponse(responseCode = "200", description = "Department found")
    @PostMapping("/getByName")
    public ResponseEntity<DepartmentResponse> getDepartmentByName(@Valid @RequestBody NameRequest request) {
        return ResponseEntity.ok(departmentService.getDepartmentByName(request));
    }

    @Operation(summary = "Check if a department exists by name")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByName")
    public ResponseEntity<Boolean> existsByName(@Valid @RequestBody NameRequest request) {
        return ResponseEntity.ok(departmentService.existsByName(request));
    }
}
