package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.*;
import com.neg.hr.human.resource.dto.create.CreateProjectDTO;
import com.neg.hr.human.resource.dto.update.UpdateProjectDTO;
import com.neg.hr.human.resource.entity.Project;

public class ProjectMapper {

    public static ProjectDTO toDTO(Project project) {
        if (project == null) return null;
        return new ProjectDTO(project.getId(), project.getName());
    }

    public static Project toEntity(CreateProjectDTO dto) {
        if (dto == null) return null;
        return Project.builder()
                .name(dto.getName())
                .build();
    }

    public static void updateEntity(Project project, UpdateProjectDTO dto) {
        if (project == null || dto == null) return;
        if (dto.getName() != null) project.setName(dto.getName());
    }
}
