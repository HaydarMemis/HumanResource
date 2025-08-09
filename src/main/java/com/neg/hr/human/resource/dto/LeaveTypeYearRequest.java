package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveTypeYearRequest {
    @NotNull
    private Long leaveTypeId;

    @NotNull
    @Min(1900)
    private Integer year;
}