package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePositionDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Base salary is required")
    @Digits(integer=10, fraction=2, message = "Base salary must be a valid monetary amount")
    private BigDecimal baseSalary;
}
