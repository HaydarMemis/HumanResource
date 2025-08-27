package com.neg.technology.human.resource.leave.model.response;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeLeaveRequestStatusResponse {

    private Long id;
    private String employeeFirstName;
    private String employeeLastName;
    private String leaveTypeName;

    private String oldStatus;
    private String newStatus;
    private String approvalNote;

    private LocalDateTime updatedAt;
}