package com.neg.technology.human.resource.Company.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyIdRequest {
    @NotNull
    private Long companyId;
}
