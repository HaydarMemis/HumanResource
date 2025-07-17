package com.neg.hr.human.resouce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "leave_balance")
@Getter
@Setter
public class LeaveBalance extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @ManyToOne
    private LeaveType leaveType;

    private Integer date; // year

    private BigDecimal amount; // total entitled leave
}
