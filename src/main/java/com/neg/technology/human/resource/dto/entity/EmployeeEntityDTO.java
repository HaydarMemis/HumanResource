package com.neg.technology.human.resource.dto.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeEntityDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String departmentName;
    private String positionTitle;
    private String managerFirstName;
    private String managerLastName;
    private String companyName;
}
