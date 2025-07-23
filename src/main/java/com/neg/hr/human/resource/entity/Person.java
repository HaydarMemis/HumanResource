package com.neg.hr.human.resource.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "person")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "national_id", length = 20)
    private String nationalId;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String gender;

    @Column(columnDefinition = "TEXT")
    private String email;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "marital_status")
    private String maritalStatus;
}
