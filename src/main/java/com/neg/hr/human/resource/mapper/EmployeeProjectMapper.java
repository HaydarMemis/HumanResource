package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.create.CreateEmployeeProjectRequestDTO;
import com.neg.hr.human.resource.dto.EmployeeProjectEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateEmployeeProjectRequestDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.EmployeeProject;
import com.neg.hr.human.resource.entity.Project;

public class EmployeeProjectMapper {
    public static EmployeeProjectEntityDTO toDTO(EmployeeProject employeeProject) {
        if (employeeProject == null) return null;

        return EmployeeProjectEntityDTO.builder()
                .id(employeeProject.getId())
                .projectName(employeeProject.getProject().getName())
                .employeeFirstName(employeeProject.getEmployee().getPerson().getFirstName())
                .employeeLastName(employeeProject.getEmployee().getPerson().getLastName())
                .build();
    }

    public static EmployeeProject toEntity(CreateEmployeeProjectRequestDTO dto, Employee employee, Project project) {
        return EmployeeProject.builder()
                .employee(employee)
                .project(project)
                .build();
    }

    public static void updateEntity(EmployeeProject existing, UpdateEmployeeProjectRequestDTO dto,
                                    Employee employee, Project project) {
        if (employee != null) existing.setEmployee(employee);
        if (project != null) existing.setProject(project);
    }
}
