package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NationalIdRequest {
    @NotNull
    private String nationalId;
}