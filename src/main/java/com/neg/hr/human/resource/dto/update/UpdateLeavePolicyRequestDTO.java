package com.neg.hr.human.resource.dto.update;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLeavePolicyRequestDTO {

    @NotNull(message = "ID cannot be null")
    private Long id; // Güncellenecek policy'nin ID'si

    private String policyType; // Örn: annual, birthday, maternity, paternity, bereavement, marriage, military, borrow, officialHoliday

    private Long employeeId;

    private LocalDate date;

    private Boolean multiplePregnancy;

    private String relation;

    private Boolean firstMarriage;

    private Boolean hasMarriageCertificate;

    private Integer requestedDays;

    private Integer currentBorrowed;
}
