package com.neg.technology.human.resource.company.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLeaveBalanceRequest {
    private Long employeeId;
    private Long leaveTypeId;
    private Integer days;  // <-- bu satırı ekle
}

