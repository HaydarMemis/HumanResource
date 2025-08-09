package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IntegerRequest {
    @NotNull
    private Integer value;
}