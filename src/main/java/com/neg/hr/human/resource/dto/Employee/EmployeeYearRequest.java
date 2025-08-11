package com.neg.hr.human.resource.dto.Employee;

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