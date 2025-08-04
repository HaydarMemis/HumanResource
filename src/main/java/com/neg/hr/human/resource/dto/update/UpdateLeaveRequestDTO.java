package com.neg.hr.human.resource.dto.update;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLeaveRequestDTO {

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
