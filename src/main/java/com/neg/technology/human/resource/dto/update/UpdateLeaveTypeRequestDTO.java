package com.neg.technology.human.resource.dto.update;

import com.neg.technology.human.resource.entity.LeaveType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLeaveTypeRequestDTO {
    @NotNull
    private Long id;

    @NotNull(message = "Leave type name must not be null")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "isAnnual must not be null")
    private Boolean isAnnual;

    @NotNull(message = "genderRequired must not be null")
    private LeaveType.Gender genderRequired;

    private Integer defaultDays;

    private Integer validAfterDays;

    private Integer validUntilDays;

    @NotNull(message = "isUnpaid must not be null")
    private Boolean isUnpaid;

    private String resetPeriod;

    private Integer borrowableLimit;
}