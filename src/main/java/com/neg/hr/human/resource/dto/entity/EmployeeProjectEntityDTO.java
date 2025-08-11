package com.neg.hr.human.resource.dto.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeProjectEntityDTO {
    private Long id;
    private String projectName;
    private String employeeFirstName;
    private String employeeLastName;
}
