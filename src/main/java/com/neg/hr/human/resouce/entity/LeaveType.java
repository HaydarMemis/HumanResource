package com.neg.hr.human.resouce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "leave_type")
@Getter
@Setter
public class LeaveType extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Boolean isAnnual;
    private Boolean genderRequired;
    private Integer defaultDays;
    private Integer validAfterDays;
    private Integer validUntilDays;
    private Boolean isUnpaid;
    private String resetPeriod;
    private Integer borrowableLimit;
}
