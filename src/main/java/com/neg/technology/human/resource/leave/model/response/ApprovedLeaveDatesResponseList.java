package com.neg.technology.human.resource.leave.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApprovedLeaveDatesResponseList {
    private List<ApprovedLeaveDatesResponse> leaves;
}
