package com.neg.hr.human.resource.dto.LeavePolicy;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeavePolicyResponseDTO {

    private Boolean eligible;

    private Integer days;
}
