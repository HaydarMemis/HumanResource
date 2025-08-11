package com.neg.hr.human.resource.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LeavePolicyResponseDTO {

    private Boolean eligible; // Uygunluk durumu (true/false)

    private Integer days; // İzin gün sayısı (varsa)
}
