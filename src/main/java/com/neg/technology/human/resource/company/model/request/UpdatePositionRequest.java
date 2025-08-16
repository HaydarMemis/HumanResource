package com.neg.technology.human.resource.company.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePositionRequest {
    @NotNull
    private Long id;

    @NotBlank(message = "Title must not be empty")
    private String title;

    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary must be zero or positive")
    private BigDecimal baseSalary;
}