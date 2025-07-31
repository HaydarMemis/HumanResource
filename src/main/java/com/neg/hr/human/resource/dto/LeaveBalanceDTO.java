package com.neg.hr.human.resource.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveBalanceDTO {
    private Long id;
    private String employeeFirstName;
    private String employeeLastName;
    private String leaveTypeName;
    private Integer leaveTypeBorrowableLimit;
    private Boolean leaveTypeIsUnpaid;
    private Integer date;
    private BigDecimal amount;
}
