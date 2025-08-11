package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeStatusRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    private String status;
}