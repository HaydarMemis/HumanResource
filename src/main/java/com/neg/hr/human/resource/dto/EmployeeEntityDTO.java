package com.neg.hr.human.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
