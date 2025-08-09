package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusRequest {
    @NotNull
    private String status;
}