package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.Department;
import com.neg.hr.human.resource.service.DepartmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    // Constructor injection
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        Optional<Department> departmentOpt = departmentService.findById(id);
        return departmentOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Department createDepartment(@RequestBody Department department) {
        return departmentService.save(department);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        Optional<Department> existingDepartment = departmentService.findById(id);
        if (!existingDepartment.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Department updated = departmentService.update(id, department);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        Optional<Department> existingDepartment = departmentService.findById(id);
        if (!existingDepartment.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Find by exact name
    @GetMapping("/name/{name}")
    public ResponseEntity<Department> getDepartmentByName(@PathVariable String name) {
        Optional<Department> departmentOpt = departmentService.findByName(name);
        return departmentOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Check if department exists by name
    @GetMapping("/exists/{name}")
    public boolean existsByName(@PathVariable String name) {
        return departmentService.existsByName(name);
    }

    // Find departments by exact location
    @GetMapping("/location/{location}")
    public List<Department> getDepartmentsByLocation(@PathVariable String location) {
        return departmentService.findByLocation(location);
    }

    // Find departments where location contains keyword (case-insensitive)
    @GetMapping("/location-contains/{keyword}")
    public List<Department> getDepartmentsByLocationContaining(@PathVariable String keyword) {
        return departmentService.findByLocationContainingIgnoreCase(keyword);
    }
}
