package com.neg.technology.human.resource.Department.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class DepartmentIdRequest {
    @NotNull
    private Long departmentId;
}
