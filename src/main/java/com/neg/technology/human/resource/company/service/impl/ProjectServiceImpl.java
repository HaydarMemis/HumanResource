package com.neg.technology.human.resource.company.service.impl;

import com.neg.technology.human.resource.company.service.ProjectService;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.company.model.entity.Project;
import com.neg.technology.human.resource.company.model.mapper.ProjectMapper;
import com.neg.technology.human.resource.company.model.request.*;
import com.neg.technology.human.resource.company.model.response.*;
import com.neg.technology.human.resource.company.repository.ProjectRepository;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final String MESSAGE = "Project";
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Mono<ProjectResponseList> getAllProjects() {
        return Mono.fromCallable(() ->
                new ProjectResponseList(
                        projectRepository.findAll()
                                .stream()
                                .map(ProjectMapper::toDTO)
                                .toList()
                )
        );
    }

    @Override
    public Mono<ProjectResponse> getProjectById(ProjectIdRequest request) {
        return Mono.fromCallable(() ->
                projectRepository.findById(request.getProjectId())
                        .map(ProjectMapper::toDTO)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getProjectId()))
        );
    }

    @Override
    public Mono<ProjectResponse> getProjectByName(NameRequest request) {
        return Mono.fromCallable(() ->
                projectRepository.findByName(request.getName())
                        .map(ProjectMapper::toDTO)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getName()))
        );
    }

    @Override
    public Mono<ProjectResponse> createProject(CreateProjectRequest request) {
        return Mono.fromCallable(() -> {
            Project entity = ProjectMapper.toEntity(request);
            Project saved = projectRepository.save(entity);
            Logger.logCreated(Project.class, saved.getId(), saved.getName());
            return ProjectMapper.toDTO(saved);
        });
    }

    @Override
    public Mono<ProjectResponse> updateProject(UpdateProjectRequest request) {
        return Mono.fromCallable(() -> {
            Project existing = projectRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

            existing.setName(request.getName());
            Project updated = projectRepository.save(existing);
            Logger.logUpdated(Project.class, updated.getId(), updated.getName());

            return ProjectMapper.toDTO(updated);
        });
    }

    @Override
    public Mono<Void> deleteProject(ProjectIdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!projectRepository.existsById(request.getProjectId())) {
                throw new ResourceNotFoundException(MESSAGE, request.getProjectId());
            }
            projectRepository.deleteById(request.getProjectId());
            Logger.logDeleted(Project.class, request.getProjectId());
        });
    }

    @Override
    public Mono<Boolean> existsByName(NameRequest request) {
        return Mono.fromCallable(() -> projectRepository.existsByName(request.getName()));
    }

    // Helpers
    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }
}
