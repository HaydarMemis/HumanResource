package com.neg.technology.human.resource.Project.service;

import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.Project.model.entity.Project;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Project.repository.ProjectRepository;
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
    public Project save(Project project) {
        Project saved = projectRepository.save(project);
        BusinessLogger.logCreated(Project.class, saved.getId(), saved.getName());
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
        if(!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project", id);
        }
        projectRepository.deleteById(id);
        BusinessLogger.logDeleted(Project.class, id);
    }

    @Override
    public Project update(Long id, Project project) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        existing.setName(project.getName());

        Project updated = projectRepository.save(existing);
        BusinessLogger.logUpdated(Project.class, updated.getId(), updated.getName());
        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }
}
