package com.neg.hr.human.resource.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCompanyRequestDTO {
    private Long id;
    @NotBlank(message = "Company name must not be empty")
    @Size(max = 100, message = "Company name must be at most 100 characters")
    private String name;
}
