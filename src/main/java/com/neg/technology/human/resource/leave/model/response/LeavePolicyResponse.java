package com.neg.technology.human.resource.leave.model.response;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeavePolicyResponse {

    private Boolean eligible;

    private BigDecimal days;
}
