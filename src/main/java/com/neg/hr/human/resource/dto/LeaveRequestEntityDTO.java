package com.neg.hr.human.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestEntityDTO {

    private Long id;

    // Employee names only
    private String employeeFirstName;
    private String employeeLastName;

    // LeaveType name only
    private String leaveTypeName;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal requestedDays;
    private String status;
    private String reason;

    // ApprovedBy employee names only
    private String approvedByFirstName;
    private String approvedByLastName;

    private LocalDateTime approvedAt;
    private String approvalNote;
    private Boolean isCancelled;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
}
