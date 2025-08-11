package com.neg.technology.human.resource.dto.entity;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionEntityDTO {

    private Long id;

    private String title;

    private BigDecimal baseSalary;
}
