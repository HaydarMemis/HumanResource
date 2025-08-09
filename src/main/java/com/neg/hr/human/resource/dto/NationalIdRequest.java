package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NationalIdRequest {
    @NotNull
    private String nationalId;
}