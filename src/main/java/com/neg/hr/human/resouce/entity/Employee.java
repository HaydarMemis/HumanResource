package com.neg.hr.human.resouce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Getter
@Setter
public class Employee extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Person person;

    @ManyToOne
    private Department department;

    @ManyToOne
    private Position position;

    @ManyToOne
    private Company company;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    private String registrationNumber;
    private LocalDate hireDate;
    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;

    private Boolean isActive;
}
