package com.neg.hr.human.resource.dto.utilities;

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