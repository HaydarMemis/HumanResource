package com.neg.hr.human.resouce.controller;

import com.neg.hr.human.resouce.entity.EmployeeProject;
import com.neg.hr.human.resouce.service.EmployeeProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee-projects")
public class EmployeeProjectController {

    private final EmployeeProjectService employeeProjectService;

    public EmployeeProjectController(EmployeeProjectService employeeProjectService) {
        this.employeeProjectService = employeeProjectService;
    }

    // Tüm kayıtları getir
    @GetMapping
    public List<EmployeeProject> getAll() {
        return employeeProjectService.findAll();
    }

    // ID’ye göre getir
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeProject> getById(@PathVariable Long id) {
        Optional<EmployeeProject> ep = employeeProjectService.findById(id);
        return ep.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Yeni kayıt oluştur
    @PostMapping
    public EmployeeProject create(@RequestBody EmployeeProject employeeProject) {
        return employeeProjectService.save(employeeProject);
    }

    // Güncelle
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeProject> update(@PathVariable Long id, @RequestBody EmployeeProject employeeProject) {
        try {
            EmployeeProject updated = employeeProjectService.update(id, employeeProject);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ID’ye göre sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        Optional<EmployeeProject> existing = employeeProjectService.findById(id);
        if (existing.isPresent()) {
            employeeProjectService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Çalışan ID’sine göre sil
    @DeleteMapping("/employee/{employeeId}")
    public ResponseEntity<Void> deleteByEmployeeId(@PathVariable Long employeeId) {
        employeeProjectService.deleteByEmployeeId(employeeId);
        return ResponseEntity.noContent().build();
    }

    // Proje ID’sine göre sil
    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Void> deleteByProjectId(@PathVariable Long projectId) {
        employeeProjectService.deleteByProjectId(projectId);
        return ResponseEntity.noContent().build();
    }

    // Çalışan ID’sine göre listele
    @GetMapping("/employee/list/{employeeId}")
    public List<EmployeeProject> getByEmployeeId(@PathVariable Long employeeId) {
        return employeeProjectService.findByEmployeeId(employeeId);
    }

    // Proje ID’sine göre listele
    @GetMapping("/project/list/{projectId}")
    public List<EmployeeProject> getByProjectId(@PathVariable Long projectId) {
        return employeeProjectService.findByProjectId(projectId);
    }

    // Belirli çalışan ve proje için kayıt var mı?
    @GetMapping("/exists")
    public boolean existsByEmployeeAndProject(
            @RequestParam Long employeeId,
            @RequestParam Long projectId) {
        return employeeProjectService.existsByEmployeeIdAndProjectId(employeeId, projectId);
    }
}
