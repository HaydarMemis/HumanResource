package com.neg.technology.human.resource.employee.model.response;

import java.util.List;

public class EmployeeProjectResponseList {
    private List<EmployeeProjectResponse> employeeProjects;

    public EmployeeProjectResponseList() {}

    public EmployeeProjectResponseList(List<EmployeeProjectResponse> employeeProjects) {
        this.employeeProjects = employeeProjects;
    }

    public List<EmployeeProjectResponse> getEmployeeProjects() {
        return employeeProjects;
    }

    public void setEmployeeProjects(List<EmployeeProjectResponse> employeeProjects) {
        this.employeeProjects = employeeProjects;
    }
}
