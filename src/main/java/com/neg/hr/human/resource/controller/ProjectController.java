package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.Project;
import com.neg.hr.human.resource.service.impl.ProjectServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectServiceImpl projectService;

    public ProjectController(ProjectServiceImpl projectService) {
        this.projectService = projectService;
    }

    // Tüm projeleri getir
    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.findAll();
    }

    // ID'ye göre proje getir
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> projectOpt = projectService.findById(id);
        return projectOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // İsimle proje getir
    @GetMapping("/name/{name}")
    public ResponseEntity<Project> getProjectByName(@PathVariable String name) {
        Optional<Project> projectOpt = projectService.findByName(name);
        return projectOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Yeni proje oluştur
    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.save(project);
    }

    // Proje güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        Optional<Project> existingProject = projectService.findById(id);
        if (!existingProject.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        project.setId(id);
        Project updated = projectService.save(project);
        return ResponseEntity.ok(updated);
    }

    // Proje sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        Optional<Project> existing = projectService.findById(id);
        if (existing.isPresent()) {
            projectService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // İsimle proje var mı kontrolü
    @GetMapping("/exists/{name}")
    public boolean existsByName(@PathVariable String name) {
        return projectService.existsByName(name);
    }
}
