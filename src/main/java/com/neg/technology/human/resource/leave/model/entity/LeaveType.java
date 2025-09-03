package com.neg.technology.human.resource.leave.model.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.neg.technology.human.resource.utility.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leave_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveType extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_annual", nullable = false)
    private Boolean isAnnual;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_required")
    private Gender genderRequired;

    @Column(name = "default_days")
    private Integer defaultDays;

    @Column(name = "valid_after_days")
    private Integer validAfterDays;

    @Column(name = "valid_until_days")
    private Integer validUntilDays;

    @Column(name = "is_unpaid", nullable = false)
    private Boolean isUnpaid;

    @Column(name = "reset_period")
    private String resetPeriod;

    @Column(name = "borrowable_limit")
    private Integer borrowableLimit;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public enum Gender {
        MALE, FEMALE, NONE;

        @JsonCreator
        public static Gender fromString(String value) {
            if (value == null) return null;
            return Gender.valueOf(value.trim().toUpperCase());
        }
    }
}