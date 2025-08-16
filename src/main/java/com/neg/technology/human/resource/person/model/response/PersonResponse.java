package com.neg.technology.human.resource.person.model.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonResponse {

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
