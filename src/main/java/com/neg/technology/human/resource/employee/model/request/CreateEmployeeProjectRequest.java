package com.neg.technology.human.resource.employee.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateEmployeeProjectRequest {
    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    @NotNull(message = "Project ID cannot be null")
    private Long projectId;
}
