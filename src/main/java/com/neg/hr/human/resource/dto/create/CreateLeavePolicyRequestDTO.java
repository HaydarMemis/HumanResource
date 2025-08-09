package com.neg.hr.human.resource.dto.create;

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
public class CreateLeavePolicyRequestDTO {

    @NotNull(message = "Policy type cannot be null")
    private String policyType; // Örn: annual, birthday, maternity, paternity, bereavement, marriage, military, borrow, officialHoliday

    private Long employeeId; // Çalışan ID

    private LocalDate date; // Doğum günü veya resmi tatil kontrolü için

    private Boolean multiplePregnancy; // Gebelik durumu (opsiyonel)

    private String relation; // Yakınlık derecesi (bereavement için)

    private Boolean firstMarriage; // İlk evlilik mi? (opsiyonel)

    private Boolean hasMarriageCertificate; // Evlilik belgesi var mı? (opsiyonel)

    private Integer requestedDays; // Borç izin talebi için

    private Integer currentBorrowed; // Zaten alınan borç izin miktarı
}
