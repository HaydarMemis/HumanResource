package com.neg.technology.human.resource.LeaveBalance.model.entity;

import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Utility.AuditableEntity;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
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

    private Integer date; // year

    @Column(nullable = false)
    private BigDecimal amount; // total entitled leave
}
