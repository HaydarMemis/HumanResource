package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
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

    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary must be zero or positive")
    private BigDecimal baseSalary;
}
