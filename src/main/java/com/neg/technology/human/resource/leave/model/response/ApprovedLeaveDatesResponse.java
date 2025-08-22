package com.neg.technology.human.resource.leave.model.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovedLeaveDatesResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveTypeName; // optional
}
