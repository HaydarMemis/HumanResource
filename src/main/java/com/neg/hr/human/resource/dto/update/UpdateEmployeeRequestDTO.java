package com.neg.hr.human.resource.dto.update;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEmployeeRequestDTO {
    private Long personId;
    private Long id;
    private Long departmentId;
    private Long positionId;
    private Long companyId;
    private Long managerId;

    private String registrationNumber;

    private LocalDateTime hireDate;
    private LocalDateTime employmentStartDate;
    private LocalDateTime employmentEndDate;

    private Boolean isActive;
}
