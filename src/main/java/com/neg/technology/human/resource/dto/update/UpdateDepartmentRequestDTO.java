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
public class UpdateDepartmentRequestDTO {
    @NotBlank(message = "Department name must not be empty")
    @Size(max = 100, message = "Department name must be at most 100 characters")
    private String name;
    @NotNull(message = "Department ID is required")
    private Long id;
    private String location;
}
