package com.neg.hr.human.resource.dto;

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
public class LeavePolicyRequestDTO {

    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    private LocalDate date; // Doğum günü, resmi tatil veya diğer tarih bazlı kontroller için

    private Boolean multiplePregnancy; // Gebelik durumu

    private String relationType; // Yakınlık derecesi (bereavement)

    private Boolean firstMarriage; // İlk evlilik mi?

    private Boolean isSpouseWorking; // Eş çalışıyor mu?

    private Integer requestedDays; // Borç izin talebi için

    private Integer currentBorrowed; // Zaten alınan borç izin miktarı
}
