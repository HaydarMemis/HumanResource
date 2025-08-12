package com.neg.technology.human.resource.Employee.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeYearRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    @Min(1900)
    private Integer year;
}