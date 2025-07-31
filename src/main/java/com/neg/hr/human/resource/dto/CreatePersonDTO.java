package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePersonDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @Size(max = 20, message = "National ID must be at most 20 characters")
    private String nationalId;

    @PastOrPresent(message = "Birth date cannot be in the future")
    private LocalDate birthDate;

    private String gender;

    @Email(message = "Email should be valid")
    private String email;

    private String phone;

    private String address;

    private String maritalStatus;
}
