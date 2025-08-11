package com.neg.technology.human.resource.dto.create;

import com.neg.technology.human.resource.entity.LeaveType.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateLeaveTypeRequestDTO {

    @NotNull(message = "Leave type name must not be null")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "isAnnual must not be null")
    private Boolean isAnnual;

    @NotNull(message = "genderRequired must not be null")
    private Gender genderRequired;

    private Integer defaultDays;

    private Integer validAfterDays;

    private Integer validUntilDays;

    @NotNull(message = "isUnpaid must not be null")
    private Boolean isUnpaid;

    private String resetPeriod;

    private Integer borrowableLimit;
}
