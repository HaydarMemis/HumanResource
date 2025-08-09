package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TitleRequest {
    @NotBlank
    private String title;
}