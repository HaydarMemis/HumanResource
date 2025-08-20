package com.neg.technology.human.resource.company.controller;

import com.neg.technology.human.resource.company.model.request.CreateProjectRequest;
import com.neg.technology.human.resource.company.model.request.ProjectIdRequest;
import com.neg.technology.human.resource.company.model.request.UpdateProjectRequest;
import com.neg.technology.human.resource.company.model.response.ProjectResponse;
import com.neg.technology.human.resource.company.model.response.ProjectResponseList;
import com.neg.technology.human.resource.company.service.ProjectService;
import com.neg.technology.human.resource.company.validator.ProjectValidator;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectValidator projectValidator;

    @Operation(summary = "Get all projects", description = "Returns a list of all projects registered in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<ProjectResponseList>> getAllProjects() {
        return projectService.getAllProjects()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get project by ID", description = "Returns the project with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Project found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<ProjectResponse>> getProjectById(@Valid @RequestBody ProjectIdRequest request) {
        return projectService.getProjectById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get project by name", description = "Returns the project with the specified name.")
    @ApiResponse(responseCode = "200", description = "Project found")
    @PostMapping("/getByName")
    public Mono<ResponseEntity<ProjectResponse>> getProjectByName(@Valid @RequestBody NameRequest request) {
        return projectService.getProjectByName(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Create new project", description = "Creates a new project record.")
    @ApiResponse(responseCode = "200", description = "Project successfully created")
    @PostMapping("/create")
    public Mono<ResponseEntity<ProjectResponse>> createProject(@Valid @RequestBody CreateProjectRequest request) {
        return projectValidator.validateCreate(request)
                .then(projectService.createProject(request))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update project", description = "Updates an existing project.")
    @ApiResponse(responseCode = "200", description = "Project successfully updated")
    @PostMapping("/update")
    public Mono<ResponseEntity<ProjectResponse>> updateProject(@Valid @RequestBody UpdateProjectRequest request) {
        return projectValidator.validateUpdate(request, request.getId())
                .then(projectService.updateProject(request))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete project", description = "Deletes the project with the specified ID.")
    @ApiResponse(responseCode = "204", description = "Project successfully deleted")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deleteProject(@Valid @RequestBody ProjectIdRequest request) {
        return projectService.deleteProject(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Check if project exists by name", description = "Checks whether a project with the given name exists.")
    @ApiResponse(responseCode = "200", description = "Boolean result indicating existence")
    @PostMapping("/existsByName")
    public Mono<ResponseEntity<Boolean>> existsByName(@Valid @RequestBody NameRequest request) {
        return projectService.existsByName(request)
                .map(ResponseEntity::ok);
    }
}