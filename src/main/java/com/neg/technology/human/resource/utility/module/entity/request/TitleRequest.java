package com.neg.technology.human.resource.utility.module.entity.request;

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