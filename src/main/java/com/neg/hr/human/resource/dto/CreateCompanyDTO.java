package com.neg.hr.human.resource.dto;

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
public class CreateCompanyDTO {

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must be at most 100 characters")
    private String name;
}
