package com.neg.technology.human.resource.leave.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeLeaveRequestStatusResponseList {
    private List<ChangeLeaveRequestStatusResponse> responses;
}
