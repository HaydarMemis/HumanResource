package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.EmployeeProjectDTO;
import com.neg.hr.human.resource.entity.EmployeeProject;

public class EmployeeProjectMapper {
    public static EmployeeProjectDTO toDTO(EmployeeProject employeeProject) {
        if (employeeProject == null) return null;

        return EmployeeProjectDTO.builder()
                .id(employeeProject.getId())
                .projectName(employeeProject.getProject().getName())
                .build();
    }
}
