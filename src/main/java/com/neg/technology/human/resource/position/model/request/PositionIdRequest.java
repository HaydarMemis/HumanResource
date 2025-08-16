package com.neg.technology.human.resource.position.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PositionIdRequest {
    @NotNull
    private Long positionId;
}
