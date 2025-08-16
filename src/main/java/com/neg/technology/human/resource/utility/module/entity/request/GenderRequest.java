package com.neg.technology.human.resource.utility.module.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenderRequest {
    @NotNull
    private String gender;
}