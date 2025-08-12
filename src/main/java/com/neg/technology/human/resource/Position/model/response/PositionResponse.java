package com.neg.technology.human.resource.Position.model.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionResponse {

    private Long id;

    private String title;

    private BigDecimal baseSalary;
}
