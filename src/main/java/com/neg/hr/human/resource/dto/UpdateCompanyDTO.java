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
public class UpdateCompanyDTO {
    @Size(max = 50, message = "Company name must be at most 100 characters")
    private String name;
}
