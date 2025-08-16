package com.neg.technology.human.resource.employee_project.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeProjectResponse {
    private Long id;
    private String projectName;
    private String employeeFirstName;
    private String employeeLastName;
}
