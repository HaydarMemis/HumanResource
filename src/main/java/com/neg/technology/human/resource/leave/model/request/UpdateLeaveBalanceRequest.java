package com.neg.technology.human.resource.leave.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateLeaveBalanceRequest {
    @NotNull
    private Long id;

    private Long employeeId;
    private Long leaveTypeId;
    private BigDecimal totalAmount; // Güncellenebilir toplam hak
    private Integer usedDays;       // Kullanılan gün
}
