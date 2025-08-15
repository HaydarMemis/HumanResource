package com.neg.technology.human.resource.Project.service;

import com.neg.technology.human.resource.Project.model.entity.Project;
import com.neg.technology.human.resource.Project.model.request.CreateProjectRequest;
import com.neg.technology.human.resource.Project.model.request.ProjectIdRequest;
import com.neg.technology.human.resource.Project.model.request.UpdateProjectRequest;
import com.neg.technology.human.resource.Project.model.response.ProjectResponse;
import com.neg.technology.human.resource.Project.model.response.ProjectResponseList;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    ProjectResponseList getAllProjects();

    ProjectResponse getProjectById(ProjectIdRequest request);

    ProjectResponse getProjectByName(NameRequest request);

    ProjectResponse createProject(CreateProjectRequest request);

    ProjectResponse updateProject(UpdateProjectRequest request);

    void deleteProject(ProjectIdRequest request);

    boolean existsByName(NameRequest request);

    Project save(Project project);

    Optional<Project> findById(Long id);

    Optional<Project> findByName(String name);

    List<Project> findAll();

    boolean existsByName(String name);

    void deleteById(Long id);

    Project update(Long id, Project project);

    boolean existsById(Long id);
}
