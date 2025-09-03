package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveBalanceRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class LeaveBalanceValidator {

    public void validateCreateDTO(CreateLeaveBalanceRequest dto) {
        if (dto.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID cannot be null");
        }
        if (dto.getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave type ID cannot be null");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Leave amount must be greater than or equal to 0");
        }
    }

    public void validateUpdateDTO(UpdateLeaveBalanceRequest dto) {
        if (dto.getEmployeeId() == null) {
            throw new IllegalArgumentException("Employee ID cannot be null for update");
        }
        if (dto.getLeaveTypeId() == null) {
            throw new IllegalArgumentException("Leave type ID cannot be null for update");
        }
        if (dto.getAmount() != null && dto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Leave amount must be greater than or equal to 0");
        }
    }
}
