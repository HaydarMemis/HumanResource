package com.neg.technology.human.resource.utility.module.entity.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalaryRequest {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salary;
}