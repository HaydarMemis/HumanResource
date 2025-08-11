package com.neg.hr.human.resource.dto.update;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLeavePolicyRequestDTO {

    @NotNull(message = "ID cannot be null")
    private Long id;
    private String policyType;

    private Long employeeId;

    private LocalDate date;

    private Boolean multiplePregnancy;

    private String relation;

    private Boolean firstMarriage;

    private Boolean hasMarriageCertificate;

    private Integer requestedDays;

    private Integer currentBorrowed;
}
