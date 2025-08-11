package com.neg.hr.human.resource.dto.update;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEmployeeProjectRequestDTO {
    private Long id;
    private Long employeeId;
    private Long projectId;
}
