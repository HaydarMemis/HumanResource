package com.neg.technology.human.resource.dto.LeaveType;

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
