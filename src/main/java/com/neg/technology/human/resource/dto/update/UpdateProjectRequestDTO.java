package com.neg.technology.human.resource.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProjectRequestDTO {
    @NotNull
    private Long id;

    @NotBlank(message = "Project name must not be empty")
    @Size(max = 255, message = "Project name must be at most 255 characters")
    private String name;
}