package com.neg.technology.human.resource.LeaveRequest.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLeaveRequestRequest {
    @NotNull
    private Long id;

    private Long employeeId;

    private Long leaveTypeId;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal requestedDays;

    @Size(min = 3, max = 50)
    private String status;

    private String reason;

    private Long approvedById;

    private LocalDateTime approvedAt;

    private String approvalNote;

    private Boolean isCancelled;

    private LocalDateTime cancelledAt;

    private String cancellationReason;
}
