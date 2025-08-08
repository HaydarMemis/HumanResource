package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.validator.ProjectValidator;
import com.neg.hr.human.resource.dto.create.CreateProjectRequestDTO;
import com.neg.hr.human.resource.dto.ProjectEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateProjectRequestDTO;
import com.neg.hr.human.resource.entity.Project;
import com.neg.hr.human.resource.mapper.ProjectMapper;
import com.neg.hr.human.resource.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;

    public ProjectController(ProjectService projectService, ProjectValidator projectValidator) {
        this.projectService = projectService;
        this.projectValidator = projectValidator;
    }

    @GetMapping
    public List<ProjectEntityDTO> getAllProjects() {
        return projectService.findAll()
                .stream()
                .map(ProjectMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectEntityDTO> getProjectById(@PathVariable Long id) {
        Optional<Project> opt = projectService.findById(id);
        return opt.map(project -> ResponseEntity.ok(ProjectMapper.toDTO(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ProjectEntityDTO> getProjectByName(@PathVariable String name) {
        Optional<Project> opt = projectService.findByName(name);
        return opt.map(project -> ResponseEntity.ok(ProjectMapper.toDTO(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProjectEntityDTO> createProject(@Valid @RequestBody CreateProjectRequestDTO dto) {
        projectValidator.validateCreate(dto);
        Project project = ProjectMapper.toEntity(dto);
        Project saved = projectService.save(project);
        return ResponseEntity.ok(ProjectMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectEntityDTO> updateProject(@PathVariable Long id, @Valid @RequestBody UpdateProjectRequestDTO dto) {
        Optional<Project> existingOpt = projectService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        projectValidator.validateUpdate(dto, id);
        Project existing = existingOpt.get();
        ProjectMapper.updateEntity(existing, dto);
        Project updated = projectService.save(existing);
        return ResponseEntity.ok(ProjectMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        Optional<Project> existingOpt = projectService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{name}")
    public boolean existsByName(@PathVariable String name) {
        return projectService.existsByName(name);
    }
}
