package com.neg.technology.human.resource.Department.controller;

import com.neg.technology.human.resource.dto.utilities.IdRequest;
import com.neg.technology.human.resource.dto.utilities.NameRequest;
import com.neg.technology.human.resource.dto.create.CreateDepartmentRequestDTO;
import com.neg.technology.human.resource.dto.entity.DepartmentEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateDepartmentRequestDTO;
import com.neg.technology.human.resource.Department.model.entity.Department;
import com.neg.technology.human.resource.Department.model.mapper.DepartmentMapper;
import com.neg.technology.human.resource.Department.service.DepartmentService;
import com.neg.technology.human.resource.Department.validator.DepartmentValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<DepartmentEntityDTO>> getAllDepartments() {
        List<DepartmentEntityDTO> departments = departmentService.findAll()
                .stream()
                .map(DepartmentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(departments);
    }

    @Operation(summary = "Get department by ID", description = "Retrieve a department by its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<DepartmentEntityDTO> getDepartmentById(
            @Parameter(description = "ID of the department to be retrieved", required = true)
            @Valid @RequestBody IdRequest request) {
        return departmentService.findById(request.getId())
                .map(department -> ResponseEntity.ok(DepartmentMapper.toDTO(department)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get department by name", description = "Retrieve a department by its name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department found"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @PostMapping("/getByName")
    public ResponseEntity<DepartmentEntityDTO> getDepartmentByName(
            @Parameter(description = "Name of the department to be retrieved", required = true)
            @Valid @RequestBody NameRequest request) {
        return departmentService.findByName(request.getName())
                .map(department -> ResponseEntity.ok(DepartmentMapper.toDTO(department)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new department", description = "Create a new department record")
    @ApiResponse(responseCode = "200", description = "Department created successfully")
    @PostMapping("/create")
    public ResponseEntity<DepartmentEntityDTO> createDepartment(
            @Parameter(description = "Department data for creation", required = true)
            @Valid @RequestBody CreateDepartmentRequestDTO dto) {
        departmentValidator.validateCreate(dto);
        Department department = DepartmentMapper.toEntity(dto);
        Department saved = departmentService.save(department);
        return ResponseEntity.ok(DepartmentMapper.toDTO(saved));
    }

    @Operation(summary = "Update existing department", description = "Update details of an existing department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @PostMapping("/update")
    public ResponseEntity<DepartmentEntityDTO> updateDepartment(
            @Valid @RequestBody UpdateDepartmentRequestDTO dto) {
        if (!departmentService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        departmentValidator.validateUpdate(dto);
        Department existing = departmentService.findById(dto.getId()).get();
        DepartmentMapper.updateEntity(existing, dto);
        Department updated = departmentService.save(existing);
        return ResponseEntity.ok(DepartmentMapper.toDTO(updated));
    }

    @Operation(summary = "Delete department", description = "Delete a department by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteDepartment(
            @Parameter(description = "ID of the department to delete", required = true)
            @Valid @RequestBody IdRequest request) {
        if (!departmentService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        departmentService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
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
    public ResponseEntity<List<DepartmentEntityDTO>> getDepartmentsByLocation(
            @Parameter(description = "Exact location of departments to retrieve", required = true)
            @Valid @RequestBody String location) {
        List<DepartmentEntityDTO> departments = departmentService.findByLocation(location)
                .stream()
                .map(DepartmentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(departments);
    }

    @Operation(summary = "Search departments by location keyword", description = "Retrieve all departments where the location contains the given keyword (case-insensitive)")
    @ApiResponse(responseCode = "200", description = "Departments retrieved successfully")
    @PostMapping("/getByLocationContaining")
    public ResponseEntity<List<DepartmentEntityDTO>> getDepartmentsByLocationContaining(
            @Parameter(description = "Location keyword to search", required = true)
            @Valid @RequestBody String keyword) {
        List<DepartmentEntityDTO> departments = departmentService.findByLocationContainingIgnoreCase(keyword)
                .stream()
                .map(DepartmentMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(departments);
    }
}
