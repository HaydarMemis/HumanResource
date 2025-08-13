package com.neg.technology.human.resource.Project.service;

import com.neg.technology.human.resource.Utility.RequestLogger;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Project.model.entity.Project;
import com.neg.technology.human.resource.Project.model.request.CreateProjectRequest;
import com.neg.technology.human.resource.Project.model.request.UpdateProjectRequest;
import com.neg.technology.human.resource.Project.model.response.ProjectResponse;
import com.neg.technology.human.resource.Project.model.response.ProjectResponseList;
import com.neg.technology.human.resource.Project.repository.ProjectRepository;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectResponseList getAllProjects() {
        return null;
    }

    @Override
    public ProjectResponse getProjectById(IdRequest request) {
        return null;
    }

    @Override
    public ProjectResponse getProjectByName(NameRequest request) {
        return null;
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest request) {
        return null;
    }

    @Override
    public ProjectResponse updateProject(UpdateProjectRequest request) {
        return null;
    }

    @Override
    public void deleteProject(IdRequest request) {

    }

    @Override
    public boolean existsByName(NameRequest request) {
        return false;
    }

    @Override
    public Project save(Project project) {
        Project saved = projectRepository.save(project);
        RequestLogger.logCreated(Project.class, saved.getId(), saved.getName());
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
            throw new ResourceNotFoundException("Project", id);
        }
        projectRepository.deleteById(id);
        RequestLogger.logDeleted(Project.class, id);
    }

    @Override
    public Project update(Long id, Project project) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        existing.setName(project.getName());
        // diğer alanları da burada setle

        Project updated = projectRepository.save(existing);
        RequestLogger.logUpdated(Project.class, updated.getId(), updated.getName());
        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }
}
