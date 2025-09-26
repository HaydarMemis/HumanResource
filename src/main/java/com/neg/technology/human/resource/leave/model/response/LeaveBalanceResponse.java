package com.neg.technology.human.resource.leave.model.response;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveBalanceResponse {
    private Long id;
    private String employeeFirstName;
    private String employeeLastName;
    private String leaveTypeName;
    private Integer year;
    private BigDecimal totalDays;
    private BigDecimal usedDays;
    private BigDecimal availableDays;
    private BigDecimal advanceDays;
}