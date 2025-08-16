package com.neg.technology.human.resource.leave.model.entity;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.utility.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveRequest extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "start_date",nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "requested_days")
    private BigDecimal requestedDays;

    @Column(nullable = false)
    private String status;

    private String reason;

    @ManyToOne
    @JoinColumn(name = "approved_by", nullable = false)
    private Employee approvedBy;

    @Column(name = "approved_At")
    private LocalDateTime approvedAt;

    @Column(name = "approved_note")
    private String approvalNote;

    @Column(name = "is_cancelled")
    private Boolean isCancelled;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason;
}
