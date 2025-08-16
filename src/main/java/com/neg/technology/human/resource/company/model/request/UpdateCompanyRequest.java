package com.neg.technology.human.resource.company.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCompanyRequest {
    private Long id;
    @NotBlank(message = "Company name must not be empty")
    @Size(max = 100, message = "Company name must be at most 100 characters")
    private String name;
}
