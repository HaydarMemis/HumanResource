package com.neg.technology.human.resource.employee.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDateRangeRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}