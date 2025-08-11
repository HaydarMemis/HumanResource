package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.utilities.IdRequest;
import com.neg.hr.human.resource.dto.create.CreateDepartmentRequestDTO;
import com.neg.hr.human.resource.dto.entity.DepartmentEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateDepartmentRequestDTO;
import com.neg.hr.human.resource.entity.Department;
import com.neg.hr.human.resource.mapper.DepartmentMapper;
import com.neg.hr.human.resource.service.DepartmentService;
import com.neg.hr.human.resource.validator.DepartmentValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @PostMapping("/getAll")
    public List<DepartmentEntityDTO> getAllDepartments() {
        return departmentService.findAll()
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
    }

    @PostMapping("/getById")
    public ResponseEntity<DepartmentEntityDTO> getDepartmentById(@Valid @RequestBody IdRequest request) {
        return departmentService.findById(request.getId())
                .map(department -> ResponseEntity.ok(DepartmentMapper.toDTO(department)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/getByName")
    public ResponseEntity<DepartmentEntityDTO> getDepartmentByName(@Valid @RequestBody String name) {
        return departmentService.findByName(name)
                .map(department -> ResponseEntity.ok(DepartmentMapper.toDTO(department)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<DepartmentEntityDTO> createDepartment(@Valid @RequestBody CreateDepartmentRequestDTO dto) {
        departmentValidator.validateCreate(dto);
        Department department = DepartmentMapper.toEntity(dto);
        Department saved = departmentService.save(department);
        return ResponseEntity.ok(DepartmentMapper.toDTO(saved));
    }

    @PostMapping("/update")
    public ResponseEntity<DepartmentEntityDTO> updateDepartment(@Valid @RequestBody UpdateDepartmentRequestDTO dto) {
        if (!departmentService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        departmentValidator.validateUpdate(dto);
        Department existing = departmentService.findById(dto.getId()).get();
        DepartmentMapper.updateEntity(existing, dto);
        Department updated = departmentService.save(existing);
        return ResponseEntity.ok(DepartmentMapper.toDTO(updated));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteDepartment(@Valid @RequestBody IdRequest request) {
        if (!departmentService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        departmentService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/existsByName")
    public boolean existsByName(@Valid @RequestBody String name) {
        return departmentService.existsByName(name);
    }

    @PostMapping("/getByLocation")
    public List<DepartmentEntityDTO> getDepartmentsByLocation(@Valid @RequestBody String location) {
        return departmentService.findByLocation(location)
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
    }

    @PostMapping("/getByLocationContaining")
    public List<DepartmentEntityDTO> getDepartmentsByLocationContaining(@Valid @RequestBody String keyword) {
        return departmentService.findByLocationContainingIgnoreCase(keyword)
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
    }
}