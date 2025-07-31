package com.neg.hr.human.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProjectDTO {
    private Long id;
    private String projectName;
    private String employeeFirstName;
    private String employeeLastName;
}
