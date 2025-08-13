package com.neg.technology.human.resource.LeaveType.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveTypeResponseList {
    private List<LeaveTypeResponse> leaveTypes;
}
