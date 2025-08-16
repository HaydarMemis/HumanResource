package com.neg.technology.human.resource.utility.module.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailRequest {
    @NotNull
    private String email;
}