package com.neg.hr.human.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonDTO {

    private Long id;

    private String firstName;

    private String lastName;

    private String nationalId;

    private LocalDate birthDate;

    private String gender;

    private String email;

    private String phone;

    private String address;

    private String maritalStatus;
}
