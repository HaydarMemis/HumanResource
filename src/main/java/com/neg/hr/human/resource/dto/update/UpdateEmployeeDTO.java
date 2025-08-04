package com.neg.hr.human.resource.dto.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEmployeeDTO {
    private Long personId;
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
