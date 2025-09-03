package com.neg.technology.human.resource.leave.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLeaveRequestStatusRequest {
    private Long id;
    private String status;
}
