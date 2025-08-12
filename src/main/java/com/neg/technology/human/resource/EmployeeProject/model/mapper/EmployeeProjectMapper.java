package com.neg.technology.human.resource.EmployeeProject.model.mapper;

import com.neg.technology.human.resource.Project.model.entity.Project;
import com.neg.technology.human.resource.EmployeeProject.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.EmployeeProject.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.EmployeeProject.model.entity.EmployeeProject;

public class EmployeeProjectMapper {
    public static EmployeeProjectResponse toDTO(EmployeeProject employeeProject) {
        if (employeeProject == null) return null;

        return EmployeeProjectResponse.builder()
                .id(employeeProject.getId())
                .projectName(employeeProject.getProject().getName())
                .employeeFirstName(employeeProject.getEmployee().getPerson().getFirstName())
                .employeeLastName(employeeProject.getEmployee().getPerson().getLastName())
                .build();
    }

    public static EmployeeProject toEntity(CreateEmployeeProjectRequest dto, Employee employee, Project project) {
        return EmployeeProject.builder()
                .employee(employee)
                .project(project)
                .build();
    }

    public static void updateEntity(EmployeeProject existing, UpdateEmployeeProjectRequest dto,
                                    Employee employee, Project project) {
        if (employee != null) existing.setEmployee(employee);
        if (project != null) existing.setProject(project);
    }
}
