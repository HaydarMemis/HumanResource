package com.neg.technology.human.resource.dto.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveTypeEntityDTO {

    private Long id;
    private String name;
    private Boolean isAnnual;
    private String genderRequired;
    private Boolean isUnpaid;
}