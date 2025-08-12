package com.neg.technology.human.resource.LeavePolicy.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeavePolicyResponse {

    private Boolean eligible;

    private Integer days;
}
