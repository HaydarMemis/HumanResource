package com.neg.hr.human.resource.dto.update;

import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLeaveBalanceDTO {
    private Long employeeId;
    private Long leaveTypeId;

    @Min(1900)
    private Integer date;

    @Min(0)
    private BigDecimal amount;
}
