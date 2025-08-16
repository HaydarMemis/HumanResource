package com.neg.technology.human.resource.leave.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLeaveBalanceRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    private Long leaveTypeId;

    @NotNull
    @Min(1900)
    private Integer date;

    @NotNull
    @Min(0)
    private BigDecimal amount;
}
