package com.neg.technology.human.resource.EmployeeProject.model.response;

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
