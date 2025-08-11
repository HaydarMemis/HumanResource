package com.neg.technology.human.resource.dto.utilities;

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