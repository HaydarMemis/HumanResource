package com.neg.technology.human.resource.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String personFullName;
    private String departmentName;
    private String positionName;
    private String companyName;
    private String managerFullName;
    private String registrationNumber;
    private LocalDateTime hireDate;
    private LocalDateTime employmentStartDate;
    private LocalDateTime employmentEndDate;
    private Boolean isActive;
}
