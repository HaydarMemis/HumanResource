package com.neg.technology.human.resource.Utility.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdRequest {
    @NotNull
    private Long id;
}
