package com.neg.technology.human.resource.leave_balance.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLeaveBalanceRequest {
    private Long employeeId;
    private Long leaveTypeId;
    @NotNull
    private Long id;
    @Min(1900)
    private Integer date;

    @Min(0)
    private BigDecimal amount;
}
