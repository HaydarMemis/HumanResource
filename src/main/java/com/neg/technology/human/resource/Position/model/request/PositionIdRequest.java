package com.neg.technology.human.resource.Position.model.request;

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
