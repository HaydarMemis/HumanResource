package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmployeeStatusRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    private String status;
}