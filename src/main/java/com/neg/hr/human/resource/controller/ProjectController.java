package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.IdRequest;
import com.neg.hr.human.resource.dto.NameRequest;
import com.neg.hr.human.resource.dto.create.CreateProjectRequestDTO;
import com.neg.hr.human.resource.dto.entity.ProjectEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateProjectRequestDTO;
import com.neg.hr.human.resource.entity.Project;
import com.neg.hr.human.resource.mapper.ProjectMapper;
import com.neg.hr.human.resource.service.ProjectService;
import com.neg.hr.human.resource.validator.ProjectValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;

    public ProjectController(ProjectService projectService,
                             ProjectValidator projectValidator) {
        this.projectService = projectService;
        this.projectValidator = projectValidator;
    }

    // POST - get all projects
    @PostMapping("/getAll")
    public List<ProjectEntityDTO> getAllProjects() {
        return projectService.findAll()
                .stream()
                .map(ProjectMapper::toDTO)
                .toList();
    }

    // POST - get project by ID
    @PostMapping("/getById")
    public ResponseEntity<ProjectEntityDTO> getProjectById(@Valid @RequestBody IdRequest request) {
        return projectService.findById(request.getId())
                .map(project -> ResponseEntity.ok(ProjectMapper.toDTO(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - get project by name
    @PostMapping("/getByName")
    public ResponseEntity<ProjectEntityDTO> getProjectByName(@Valid @RequestBody NameRequest request) {
        return projectService.findByName(request.getName())
                .map(project -> ResponseEntity.ok(ProjectMapper.toDTO(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - create project
    @PostMapping("/create")
    public ResponseEntity<ProjectEntityDTO> createProject(@Valid @RequestBody CreateProjectRequestDTO dto) {
        projectValidator.validateCreate(dto);
        Project project = ProjectMapper.toEntity(dto);
        Project saved = projectService.save(project);
        return ResponseEntity.ok(ProjectMapper.toDTO(saved));
    }

    // POST - update project
    @PostMapping("/update")
    public ResponseEntity<ProjectEntityDTO> updateProject(@Valid @RequestBody UpdateProjectRequestDTO dto) {
        if (!projectService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        projectValidator.validateUpdate(dto, dto.getId());
        Project existing = projectService.findById(dto.getId()).get();
        ProjectMapper.updateEntity(existing, dto);
        Project updated = projectService.save(existing);
        return ResponseEntity.ok(ProjectMapper.toDTO(updated));
    }

    // POST - delete project
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteProject(@Valid @RequestBody IdRequest request) {
        if (!projectService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        projectService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    // POST - check if project exists by name
    @PostMapping("/existsByName")
    public boolean existsByName(@Valid @RequestBody NameRequest request) {
        return projectService.existsByName(request.getName());
    }
}