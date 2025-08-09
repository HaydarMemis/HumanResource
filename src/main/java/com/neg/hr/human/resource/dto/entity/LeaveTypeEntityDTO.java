package com.neg.hr.human.resource.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeEntityDTO {

    private Long id;
    private String name;
    private Boolean isAnnual;
    private String genderRequired;
    private Boolean isUnpaid;
}