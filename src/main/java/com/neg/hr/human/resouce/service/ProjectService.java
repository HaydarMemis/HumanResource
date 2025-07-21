package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Project;
import com.neg.hr.human.resouce.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService implements ProjectInterface {
    private final ProjectRepository projectRepository;

    public Project save(Project project) {
        return projectRepository.save(project);
    }

    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    public Project update(Long id, Project project) {
        Project existing = findById(id);
        existing.setName(project.getName());
        return projectRepository.save(project);
    }
}
