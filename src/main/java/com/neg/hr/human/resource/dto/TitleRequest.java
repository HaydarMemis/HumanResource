package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TitleRequest {
    @NotBlank
    private String title;
}