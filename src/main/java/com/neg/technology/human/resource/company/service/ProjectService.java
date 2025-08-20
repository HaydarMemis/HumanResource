package com.neg.technology.human.resource.company.service;
import com.neg.technology.human.resource.company.model.entity.Project;
import com.neg.technology.human.resource.company.model.request.*;
import com.neg.technology.human.resource.company.model.response.*;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProjectService {

    Mono<ProjectResponseList> getAllProjects();

    Mono<ProjectResponse> getProjectById(ProjectIdRequest request);

    Mono<ProjectResponse> getProjectByName(NameRequest request);

    Mono<ProjectResponse> createProject(CreateProjectRequest request);

    Mono<ProjectResponse> updateProject(UpdateProjectRequest request);

    Mono<Void> deleteProject(ProjectIdRequest request);

    Mono<Boolean> existsByName(NameRequest request);

    // helper methods, still return plain types for internal use if needed
    Project save(Project project);

    List<Project> findAll();
}
