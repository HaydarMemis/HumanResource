package com.neg.technology.human.resource.EmployeeProject.model.mapper;

import com.neg.technology.human.resource.dto.create.CreateEmployeeProjectRequestDTO;
import com.neg.technology.human.resource.dto.entity.EmployeeProjectEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateEmployeeProjectRequestDTO;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.EmployeeProject.model.entity.EmployeeProject;
import com.neg.technology.human.resource.Utility.Project;

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
