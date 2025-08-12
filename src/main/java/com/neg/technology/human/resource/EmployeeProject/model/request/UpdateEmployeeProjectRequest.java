package com.neg.technology.human.resource.EmployeeProject.model.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEmployeeProjectRequest {
    private Long id;
    private Long employeeId;
    private Long projectId;
}
