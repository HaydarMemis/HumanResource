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
public class UpdateLeaveTypeDTO {

    @Size(max = 255)
    private String name;

    private Boolean isAnnual;

    private Boolean genderRequired;

    private Integer defaultDays;

    private Integer validAfterDays;

    private Integer validUntilDays;

    private Boolean isUnpaid;

    private String resetPeriod;

    private Integer borrowableLimit;
}
