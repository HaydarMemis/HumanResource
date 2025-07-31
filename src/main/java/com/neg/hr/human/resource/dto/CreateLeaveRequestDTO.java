package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
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
public class CreateLeaveRequestDTO {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private BigDecimal requestedDays;

    @NotNull(message = "Status is required")
    @Size(min = 3, max = 50)
    private String status;

    private String reason;

    @NotNull(message = "Approved by employee ID is required")
    private Long approvedById;

    private LocalDateTime approvedAt;

    private String approvalNote;

    private Boolean isCancelled;

    private LocalDateTime cancelledAt;

    private String cancellationReason;
}
