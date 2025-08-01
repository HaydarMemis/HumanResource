package com.neg.hr.human.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionDTO {

    private Long id;

    private String title;

    private BigDecimal baseSalary;
}
