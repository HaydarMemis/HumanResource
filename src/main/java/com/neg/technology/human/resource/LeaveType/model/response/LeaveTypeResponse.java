package com.neg.technology.human.resource.LeaveType.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveTypeResponse {

    private Long id;
    private String name;
    private Boolean isAnnual;
    private String genderRequired;
    private Boolean isUnpaid;
}