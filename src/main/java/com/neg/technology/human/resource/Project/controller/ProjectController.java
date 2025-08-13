package com.neg.technology.human.resource.Project.controller;

import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import com.neg.technology.human.resource.Project.model.request.CreateProjectRequest;
import com.neg.technology.human.resource.Project.model.request.UpdateProjectRequest;
import com.neg.technology.human.resource.Project.model.response.ProjectResponse;
import com.neg.technology.human.resource.Project.model.response.ProjectResponseList;
import com.neg.technology.human.resource.Project.service.ProjectService;
import com.neg.technology.human.resource.Project.validator.ProjectValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;

    @Operation(summary = "Get all projects", description = "Returns a list of all projects registered in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @PostMapping("/getAll")
    public ResponseEntity<ProjectResponseList> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @Operation(summary = "Get project by ID", description = "Returns the project with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Project found")
    @PostMapping("/getById")
    public ResponseEntity<ProjectResponse> getProjectById(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(projectService.getProjectById(request));
    }

    @Operation(summary = "Get project by name", description = "Returns the project with the specified name.")
    @ApiResponse(responseCode = "200", description = "Project found")
    @PostMapping("/getByName")
    public ResponseEntity<ProjectResponse> getProjectByName(@Valid @RequestBody NameRequest request) {
        return ResponseEntity.ok(projectService.getProjectByName(request));
    }

    @Operation(summary = "Create new project", description = "Creates a new project record.")
    @ApiResponse(responseCode = "200", description = "Project successfully created")
    @PostMapping("/create")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        projectValidator.validateCreate(request);
        return ResponseEntity.ok(projectService.createProject(request));
    }

    @Operation(summary = "Update project", description = "Updates an existing project.")
    @ApiResponse(responseCode = "200", description = "Project successfully updated")
    @PostMapping("/update")
    public ResponseEntity<ProjectResponse> updateProject(@Valid @RequestBody UpdateProjectRequest request) {
        projectValidator.validateUpdate(request, request.getId());
        return ResponseEntity.ok(projectService.updateProject(request));
    }

    @Operation(summary = "Delete project", description = "Deletes the project with the specified ID.")
    @ApiResponse(responseCode = "204", description = "Project successfully deleted")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteProject(@Valid @RequestBody IdRequest request) {
        projectService.deleteProject(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if project exists by name", description = "Checks whether a project with the given name exists.")
    @ApiResponse(responseCode = "200", description = "Boolean result indicating existence")
    @PostMapping("/existsByName")
    public ResponseEntity<Boolean> existsByName(@Valid @RequestBody NameRequest request) {
        return ResponseEntity.ok(projectService.existsByName(request));
    }

}
