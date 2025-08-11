package com.neg.hr.human.resource.dto.utilities;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusRequest {
    @NotNull
    private String status;
}