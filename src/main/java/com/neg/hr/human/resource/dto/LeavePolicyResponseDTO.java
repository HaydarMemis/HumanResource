package com.neg.hr.human.resource.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeavePolicyResponseDTO {

    private Boolean eligible; // Uygunluk durumu (true/false)

    private Integer days; // İzin gün sayısı (varsa)
}
