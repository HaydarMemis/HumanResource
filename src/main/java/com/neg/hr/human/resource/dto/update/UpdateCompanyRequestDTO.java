package com.neg.hr.human.resource.dto.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompanyRequestDTO {
    private Long id;
    @NotBlank(message = "Company name must not be empty")
    @Size(max = 100, message = "Company name must be at most 100 characters")
    private String name;
}
