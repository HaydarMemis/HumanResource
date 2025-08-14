package com.neg.technology.human.resource.Employee.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class EmployeeIdRequest {
    @NotNull
    private Long employeeId;
}
