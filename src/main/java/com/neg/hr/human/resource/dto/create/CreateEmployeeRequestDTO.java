package com.neg.hr.human.resource.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEmployeeRequestDTO {
    @NotNull(message = "Person ID is required")
    private Long personId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Position ID is required")
    private Long positionId;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private Long managerId;

    private String registrationNumber;

    @NotNull(message = "Hire date is required")
    private LocalDateTime hireDate;

    @NotNull(message = "Employment start date is required")
    private LocalDateTime employmentStartDate;

    private LocalDateTime employmentEndDate;

    @NotNull(message = "isActive flag is required")
    private Boolean isActive;
}
