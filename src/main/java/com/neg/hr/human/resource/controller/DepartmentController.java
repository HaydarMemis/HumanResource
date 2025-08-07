package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.validator.DepartmentValidator;
import com.neg.hr.human.resource.dto.create.CreateDepartmentDTO;
import com.neg.hr.human.resource.dto.DepartmentDTO;
import com.neg.hr.human.resource.dto.update.UpdateDepartmentDTO;
import com.neg.hr.human.resource.entity.Department;
import com.neg.hr.human.resource.mapper.DepartmentMapper;
import com.neg.hr.human.resource.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentValidator departmentValidator;

    public DepartmentController(DepartmentService departmentService, DepartmentValidator departmentValidator) {
        this.departmentService = departmentService;
        this.departmentValidator = departmentValidator;
    }

    @GetMapping
    public List<DepartmentDTO> getAllDepartments() {
        return departmentService.findAll()
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable Long id) {
        Optional<Department> opt = departmentService.findById(id);
        return opt.map(department -> ResponseEntity.ok(DepartmentMapper.toDTO(department)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<DepartmentDTO> getDepartmentByName(@PathVariable String name) {
        Optional<Department> opt = departmentService.findByName(name);
        return opt.map(department -> ResponseEntity.ok(DepartmentMapper.toDTO(department)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody CreateDepartmentDTO dto) {
        departmentValidator.validateCreate(dto);
        Department department = DepartmentMapper.toEntity(dto);
        Department saved = departmentService.save(department);
        return ResponseEntity.ok(DepartmentMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody UpdateDepartmentDTO dto) {
        Optional<Department> existingOpt = departmentService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        departmentValidator.validateUpdate(dto, id);
        Department existing = existingOpt.get();
        DepartmentMapper.updateEntity(existing, dto);
        Department updated = departmentService.save(existing);
        return ResponseEntity.ok(DepartmentMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        Optional<Department> existingOpt = departmentService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{name}")
    public boolean existsByName(@PathVariable String name) {
        return departmentService.existsByName(name);
    }

    @GetMapping("/location/{location}")
    public List<DepartmentDTO> getDepartmentsByLocation(@PathVariable String location) {
        return departmentService.findByLocation(location)
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
    }

    @GetMapping("/location-contains/{keyword}")
    public List<DepartmentDTO> getDepartmentsByLocationContaining(@PathVariable String keyword) {
        return departmentService.findByLocationContainingIgnoreCase(keyword)
                .stream()
                .map(DepartmentMapper::toDTO)
                .toList();
    }
}
