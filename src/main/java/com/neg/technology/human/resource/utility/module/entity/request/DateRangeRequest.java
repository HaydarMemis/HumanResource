package com.neg.technology.human.resource.utility.module.entity.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DateRangeRequest {
    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}