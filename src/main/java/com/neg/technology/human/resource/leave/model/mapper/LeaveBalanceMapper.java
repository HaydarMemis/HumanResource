package com.neg.technology.human.resource.leave.model.mapper;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Component
public class LeaveBalanceMapper {

    public LeaveBalanceResponse toResponse(LeaveBalance leaveBalance) {
        if (leaveBalance == null) {
            return null;
        }

        Employee employee = leaveBalance.getEmployee();
        LeaveType leaveType = leaveBalance.getLeaveType();

        return LeaveBalanceResponse.builder()
                .id(leaveBalance.getId())
                .employeeFirstName(employee != null && employee.getPerson() != null ? employee.getPerson().getFirstName() : null)
                .employeeLastName(employee != null && employee.getPerson() != null ? employee.getPerson().getLastName() : null)
                .leaveTypeName(leaveType != null ? leaveType.getName() : null)
                .leaveTypeBorrowableLimit(leaveType != null ? leaveType.getBorrowableLimit() : null)
                .leaveTypeIsUnpaid(leaveType != null ? leaveType.getIsUnpaid() : null)
                .totalAmount(leaveBalance.getTotalAmount())
                .usedDays(leaveBalance.getUsedDays())
                .availableBalance(leaveBalance.getAvailableBalance())
                .build();
    }

    public LeaveBalanceResponseList toResponseList(List<LeaveBalance> leaveBalances) {
        List<LeaveBalanceResponse> responses = leaveBalances == null ? List.of() :
                leaveBalances.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
        return new LeaveBalanceResponseList(responses);
    }

    public LeaveBalance toEntity(CreateLeaveBalanceRequest dto, Employee employee, LeaveType leaveType) {
        if (dto == null) {
            return null;
        }

        return LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .totalAmount(dto.getTotalAmount() != null ? dto.getTotalAmount() : BigDecimal.ZERO)
                .usedDays(0)
                .build();
    }

    public void updateEntity(LeaveBalance existing, UpdateLeaveBalanceRequest dto, Employee employee, LeaveType leaveType) {
        if (existing == null || dto == null) {
            return;
        }
        if (employee != null) {
            existing.setEmployee(employee);
        }
        if (leaveType != null) {
            existing.setLeaveType(leaveType);
        }
        if (dto.getTotalAmount() != null) {
            existing.setTotalAmount(dto.getTotalAmount());
        }
        if (dto.getUsedDays() != null) {
            existing.setUsedDays(dto.getUsedDays());
        }
    }
}
