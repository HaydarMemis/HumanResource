package com.neg.technology.human.resource.leave.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeaveTypeYearRequest {
    @NotNull
    private Long leaveTypeId;

    @NotNull
    @Min(1900)
    private Integer year;
}