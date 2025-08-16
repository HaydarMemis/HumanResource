package com.neg.technology.human.resource.employee.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeLeaveTypeRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    private Long leaveTypeId;
}
