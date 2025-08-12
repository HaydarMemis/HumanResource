package com.neg.technology.human.resource.Employee.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeResponse {
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
