package com.neg.technology.human.resource.leave.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeResponse {
    private Long id;
    private String name;
    private Boolean isAnnual;
    private String genderRequired;
    private Boolean isUnpaid;

    private Integer defaultDays;
    private Integer validAfterDays;
    private Integer validUntilDays;
    private String resetPeriod; // enum ise tipini enum yap
    private Integer borrowableLimit;
}
