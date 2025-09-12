package com.neg.technology.human.resource.leave.model.entity;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.utility.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "leave_balance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveBalance extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "earned_days", nullable = false)
    private Integer earnedDays;

    @Column(name = "used_days", nullable = false)
    private Integer usedDays = 0;

    @Column(name = "carried_over_days", nullable = false)
    private Integer carriedOverDays = 0;

    public Integer getRemainingDays() {
        return earnedDays + carriedOverDays - usedDays;
    }
}
