package com.neg.hr.human.resouce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_request")
@Getter
@Setter
public class LeaveRequest extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    private LeaveType leaveType;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal requestedDays;

    private String status;
    private String reason;
    private LocalDateTime createdAt;

    @ManyToOne
    private Employee approvedBy;

    private LocalDateTime approvedAt;
    private String approvalNote;

    private Boolean isCancelled;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
}
