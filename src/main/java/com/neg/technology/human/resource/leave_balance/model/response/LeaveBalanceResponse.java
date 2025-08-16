package com.neg.technology.human.resource.leave_balance.model.response;

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
    private Integer leaveTypeBorrowableLimit;
    private Boolean leaveTypeIsUnpaid;
    private Integer date;
    private BigDecimal amount;
}
