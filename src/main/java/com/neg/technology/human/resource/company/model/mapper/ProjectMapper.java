package com.neg.technology.human.resource.company.model.mapper;

import com.neg.technology.human.resource.company.model.request.CreateProjectRequest;
import com.neg.technology.human.resource.company.model.response.ProjectResponse;
import com.neg.technology.human.resource.company.model.request.UpdateProjectRequest;
import com.neg.technology.human.resource.company.model.entity.Project;

public class ProjectMapper {

    private ProjectMapper() {}

    public static ProjectResponse toDTO(Project project) {
        if (project == null) return null;
        return new ProjectResponse(project.getId(), project.getName());
    }

    public static Project toEntity(CreateProjectRequest dto) {
        if (dto == null) return null;
        return Project.builder()
                .name(dto.getName())
                .build();
    }

    public static void updateEntity(Project project, UpdateProjectRequest dto) {
        if (project == null || dto == null) return;
        if (dto.getName() != null) project.setName(dto.getName());
    }
}
