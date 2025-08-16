package com.neg.technology.human.resource.company.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ProjectIdRequest {
    @NotNull
    private Long projectId;
}
