package com.neg.technology.human.resource.Project.validator;

import com.neg.technology.human.resource.dto.create.CreateProjectRequestDTO;
import com.neg.technology.human.resource.dto.update.UpdateProjectRequestDTO;
import com.neg.technology.human.resource.Project.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProjectValidator {

    private final ProjectService projectService;

    public ProjectValidator(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void validateCreate(CreateProjectRequestDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Project name must not be empty");
        }
        if (projectService.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Project name already exists");
        }
    }

    public void validateUpdate(UpdateProjectRequestDTO dto, Long id) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Project name must not be empty");
        }
        projectService.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Project name already exists");
            }
        });
    }
}