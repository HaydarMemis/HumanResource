package com.neg.technology.human.resource.leave.model.request;

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
    private BigDecimal usedDays;

    @NotNull
    private BigDecimal totalDays;

    @NotNull
    private Integer year;

    @Builder.Default
    @NotNull
    private BigDecimal advanceDays = BigDecimal.ZERO;

}