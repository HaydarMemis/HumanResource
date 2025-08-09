package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryRequest {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal salary;
}