package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateLeaveTypeDTO {

    @NotBlank(message = "Leave type name is required")
    private String name;

    @NotNull(message = "isAnnual flag is required")
    private Boolean isAnnual;

    @NotNull(message = "genderRequired flag is required")
    private Boolean genderRequired;

    private Integer defaultDays;

    private Integer validAfterDays;

    private Integer validUntilDays;

    @NotNull(message = "isUnpaid flag is required")
    private Boolean isUnpaid;

    private String resetPeriod;

    private Integer borrowableLimit;
}
