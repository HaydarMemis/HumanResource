package com.neg.technology.human.resource.employee_project.model.mapper;

import com.neg.technology.human.resource.project.model.entity.Project;
import com.neg.technology.human.resource.employee_project.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee_project.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.employee_project.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee_project.model.entity.EmployeeProject;

public class EmployeeProjectMapper {
    private EmployeeProjectMapper(){}

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
        if(dto == null || employee == null || project == null) return null;

        return EmployeeProject.builder()
                .employee(employee)
                .project(project)
                .build();
    }

    public static void updateEntity(EmployeeProject existing, UpdateEmployeeProjectRequest dto,
                                    Employee employee, Project project) {
        if (existing == null || dto == null) return;

        if (employee != null) existing.setEmployee(employee);
        if (project != null) existing.setProject(project);
    }
}
