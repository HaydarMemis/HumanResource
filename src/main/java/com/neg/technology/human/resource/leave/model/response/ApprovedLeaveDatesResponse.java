package com.neg.technology.human.resource.leave.model.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovedLeaveDatesResponse {
    private Long id;
    private String employeeFirstName;
    private String employeeLastName;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer requestedDays;
    private String status;
    private String reason;
}
