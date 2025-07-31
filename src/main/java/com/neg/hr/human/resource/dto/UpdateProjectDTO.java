package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProjectDTO {

    @Size(max = 255, message = "Project name must be at most 255 characters")
    private String name;
}
