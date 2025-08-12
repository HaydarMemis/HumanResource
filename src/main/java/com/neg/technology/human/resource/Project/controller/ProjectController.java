package com.neg.technology.human.resource.Project.controller;

import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import com.neg.technology.human.resource.Project.model.request.CreateProjectRequest;
import com.neg.technology.human.resource.Project.model.response.ProjectResponse;
import com.neg.technology.human.resource.Project.model.request.UpdateProjectRequest;
import com.neg.technology.human.resource.Project.model.entity.Project;
import com.neg.technology.human.resource.Project.model.mapper.ProjectMapper;
import com.neg.technology.human.resource.Project.service.ProjectService;
import com.neg.technology.human.resource.Project.validator.ProjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Get all projects", description = "Returns a list of all projects registered in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved")
    @PostMapping("/getAll")
    public List<ProjectResponse> getAllProjects() {
        return projectService.findAll()
                .stream()
                .map(ProjectMapper::toDTO)
                .toList();
    }

    @Operation(summary = "Get project by ID", description = "Returns the project with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<ProjectResponse> getProjectById(
            @Parameter(description = "Project ID") @Valid @RequestBody IdRequest request) {
        return projectService.findById(request.getId())
                .map(project -> ResponseEntity.ok(ProjectMapper.toDTO(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get project by name", description = "Returns the project with the specified name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project found",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @PostMapping("/getByName")
    public ResponseEntity<ProjectResponse> getProjectByName(
            @Parameter(description = "Project name") @Valid @RequestBody NameRequest request) {
        return projectService.findByName(request.getName())
                .map(project -> ResponseEntity.ok(ProjectMapper.toDTO(project)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new project", description = "Creates a new project record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project successfully created",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(
            @Parameter(description = "Project data to create") @Valid @RequestBody CreateProjectRequest dto) {
        projectValidator.validateCreate(dto);
        Project project = ProjectMapper.toEntity(dto);
        Project saved = projectService.save(project);
        return ResponseEntity.ok(ProjectMapper.toDTO(saved));
    }

    @Operation(summary = "Update a project", description = "Updates an existing project record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project successfully updated",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping("/update")
    public ResponseEntity<ProjectResponse> updateProject(
            @Parameter(description = "Updated project data") @Valid @RequestBody UpdateProjectRequest dto) {
        if (!projectService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        projectValidator.validateUpdate(dto, dto.getId());
        Project existing = projectService.findById(dto.getId()).get();
        ProjectMapper.updateEntity(existing, dto);
        Project updated = projectService.save(existing);
        return ResponseEntity.ok(ProjectMapper.toDTO(updated));
    }

    @Operation(summary = "Delete a project", description = "Deletes the project with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "ID of the project to delete") @Valid @RequestBody IdRequest request) {
        if (!projectService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        projectService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if project exists by name", description = "Checks whether a project with the given name exists.")
    @ApiResponse(responseCode = "200", description = "Returns true if the project exists, otherwise false")
    @PostMapping("/existsByName")
    public boolean existsByName(
            @Parameter(description = "Project name") @Valid @RequestBody NameRequest request) {
        return projectService.existsByName(request.getName());
    }
}
