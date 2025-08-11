package com.neg.hr.human.resource.dto.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveBalanceEntityDTO {
    private Long id;
    private String employeeFirstName;
    private String employeeLastName;
    private String leaveTypeName;
    private Integer leaveTypeBorrowableLimit;
    private Boolean leaveTypeIsUnpaid;
    private Integer date;
    private BigDecimal amount;
}
