package com.neg.hr.human.resource.dto.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePositionRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary must be zero or positive")
    private BigDecimal baseSalary;
}
