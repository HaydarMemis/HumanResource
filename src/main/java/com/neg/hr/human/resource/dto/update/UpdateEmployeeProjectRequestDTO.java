package com.neg.hr.human.resource.dto.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEmployeeProjectRequestDTO {
    private Long id;
    private Long employeeId;
    private Long projectId;
}
