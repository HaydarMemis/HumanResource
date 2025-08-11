package com.neg.hr.human.resource.dto.LeavePolicy;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeavePolicyRequestDTO {

    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    private LocalDate date;

    private Boolean multiplePregnancy;

    private String relationType;

    private Boolean firstMarriage;

    private Boolean isSpouseWorking;

    private Integer requestedDays;

    private Integer currentBorrowed;
}
