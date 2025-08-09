package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DateRangeRequest {
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}