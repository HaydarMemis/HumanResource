package com.neg.technology.human.resource.Project.model.mapper;

import com.neg.technology.human.resource.dto.create.CreateProjectRequestDTO;
import com.neg.technology.human.resource.dto.entity.ProjectEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateProjectRequestDTO;
import com.neg.technology.human.resource.Project.model.entity.Project;

public class ProjectMapper {

    public static ProjectEntityDTO toDTO(Project project) {
        if (project == null) return null;
        return new ProjectEntityDTO(project.getId(), project.getName());
    }

    public static Project toEntity(CreateProjectRequestDTO dto) {
        if (dto == null) return null;
        return Project.builder()
                .name(dto.getName())
                .build();
    }

    public static void updateEntity(Project project, UpdateProjectRequestDTO dto) {
        if (project == null || dto == null) return;
        if (dto.getName() != null) project.setName(dto.getName());
    }
}
