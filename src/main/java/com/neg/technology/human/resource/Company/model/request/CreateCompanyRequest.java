package com.neg.technology.human.resource.Company.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCompanyRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name must be at most 100 characters")
    private String name;
}
