package com.neg.hr.human.resource.validator;

import com.neg.hr.human.resource.dto.create.CreateProjectDTO;
import com.neg.hr.human.resource.dto.update.UpdateProjectDTO;
import com.neg.hr.human.resource.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProjectValidator {

    private final ProjectService projectService;

    public ProjectValidator(ProjectService projectService) {
        this.projectService = projectService;
    }

    public void validateCreate(CreateProjectDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Project name must not be empty");
        }
        if (projectService.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Project name already exists");
        }
    }

    public void validateUpdate(UpdateProjectDTO dto, Long id) {
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
