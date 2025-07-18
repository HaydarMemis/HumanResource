package com.neg.hr.human.resouce.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_type")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_annual",nullable = false)
    private Boolean isAnnual;

    @Column(name = "gender_required",nullable = false)
    private Boolean genderRequired;

    @Column(name = "default_days")
    private Integer defaultDays;

    @Column(name = "valid_after_days")
    private Integer validAfterDays;

    @Column(name = "valid_until_days")
    private Integer validUntilDays;

    @Column(name = "is_unpaid",nullable = false)
    private Boolean isUnpaid;

    @Column(name = "reset_period")
    private String resetPeriod;

    @Column(name = "borrowable_limit")
    private Integer borrowableLimit;
}
