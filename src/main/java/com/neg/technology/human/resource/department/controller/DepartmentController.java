package com.neg.technology.human.resource.department.controller;

import com.neg.technology.human.resource.department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import com.neg.technology.human.resource.department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.department.model.response.DepartmentResponseList;
import com.neg.technology.human.resource.department.service.DepartmentService;
import com.neg.technology.human.resource.department.validator.DepartmentValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<DepartmentResponse>> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {
        return departmentValidator.validateCreate(request)
                .then(departmentService.createDepartment(request))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update an existing department")
    @ApiResponse(responseCode = "200", description = "Department updated successfully")
    @PostMapping("/update")
    public Mono<ResponseEntity<DepartmentResponse>> updateDepartment(
            @Valid @RequestBody UpdateDepartmentRequest request) {
        return departmentValidator.validateUpdate(request)
                .then(departmentService.updateDepartment(request))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete a department by ID")
    @ApiResponse(responseCode = "204", description = "Department deleted successfully")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deleteDepartment(@Valid @RequestBody IdRequest request) {
        return departmentService.deleteDepartment(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get all departments")
    @ApiResponse(responseCode = "200", description = "List of departments retrieved successfully")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<DepartmentResponseList>> getAllDepartments() {
        return departmentService.getAllDepartments()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get a department by ID")
    @ApiResponse(responseCode = "200", description = "Department found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<DepartmentResponse>> getDepartmentById(
            @Valid @RequestBody IdRequest request) {
        return departmentService.getDepartmentById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get a department by name")
    @ApiResponse(responseCode = "200", description = "Department found")
    @PostMapping("/getByName")
    public Mono<ResponseEntity<DepartmentResponse>> getDepartmentByName(
            @Valid @RequestBody NameRequest request) {
        return departmentService.getDepartmentByName(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Check if a department exists by name")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByName")
    public Mono<ResponseEntity<Boolean>> existsByName(
            @Valid @RequestBody NameRequest request) {
        return departmentService.existsByName(request)
                .map(ResponseEntity::ok);
    }
}