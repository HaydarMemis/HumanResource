package com.neg.hr.human.resource.dto.create;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePersonRequestDTO {

    @NotNull(message = "First name is required")
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
