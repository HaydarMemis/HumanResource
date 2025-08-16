package com.neg.technology.human.resource.project.service;

import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.project.model.entity.Project;
import com.neg.technology.human.resource.project.model.mapper.ProjectMapper;
import com.neg.technology.human.resource.project.model.request.CreateProjectRequest;
import com.neg.technology.human.resource.project.model.request.ProjectIdRequest;
import com.neg.technology.human.resource.project.model.request.UpdateProjectRequest;
import com.neg.technology.human.resource.project.model.response.ProjectResponse;
import com.neg.technology.human.resource.project.model.response.ProjectResponseList;
import com.neg.technology.human.resource.project.repository.ProjectRepository;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {
    public static final String MESSAGE="Project";
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectResponseList getAllProjects() {
        return new ProjectResponseList(
                projectRepository.findAll()
                        .stream()
                        .map(ProjectMapper::toDTO)
                        .toList()
        );
    }

    @Override
    public ProjectResponse getProjectById(ProjectIdRequest request) {
        return projectRepository.findById(request.getProjectId())
                .map(ProjectMapper::toDTO).orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getProjectId()));
    }

    @Override
    public ProjectResponse getProjectByName(NameRequest request) {
        return projectRepository.findByName(request.getName())
                .map(ProjectMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getName()));
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest request) {
        Project entity = ProjectMapper.toEntity(request);
        Project saved = projectRepository.save(entity);
        Logger.logCreated(Project.class, saved.getId(), saved.getName());
        return ProjectMapper.toDTO(saved);
    }

    @Override
    public ProjectResponse updateProject(UpdateProjectRequest request) {
        Project existing = projectRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

        existing.setName(request.getName());
        Project updated = projectRepository.save(existing);
        Logger.logUpdated(Project.class, updated.getId(), updated.getName());

        return ProjectMapper.toDTO(updated);
    }

    @Override
    public void deleteProject(ProjectIdRequest request) {
        if (!projectRepository.existsById(request.getProjectId())) {
            throw new ResourceNotFoundException(MESSAGE, request.getProjectId());
        }
        projectRepository.deleteById(request.getProjectId());
        Logger.logDeleted(Project.class, request.getProjectId());
    }

    @Override
    public boolean existsByName(NameRequest request) {
        return projectRepository.existsByName(request.getName());
    }

    @Override
    public Project save(Project project) {
        Project saved = projectRepository.save(project);
        Logger.logCreated(Project.class, saved.getId(), saved.getName());
        return saved;
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Optional<Project> findByName(String name) {
        return projectRepository.findByName(name);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public boolean existsByName(String name) {
        return projectRepository.existsByName(name);
    }

    @Override
    public void deleteById(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException(MESSAGE, id);
        }
        projectRepository.deleteById(id);
        Logger.logDeleted(Project.class, id);
    }

    @Override
    public Project update(Long id, Project project) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, id));

        existing.setName(project.getName());

        Project updated = projectRepository.save(existing);
        Logger.logUpdated(Project.class, updated.getId(), updated.getName());
        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }
}
