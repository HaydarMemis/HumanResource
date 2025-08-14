package com.neg.technology.human.resource.EmployeeProject.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EmployeeProjectIdRequest {
    @NotNull
    private Long employeeProjectId;
}
