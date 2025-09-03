package com.neg.technology.human.resource.leave.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeavePolicyResponse {

    private Boolean eligible;

    private Integer days;

    private String message;
}
