package com.neg.technology.human.resource.mapper;

import com.neg.technology.human.resource.dto.create.CreateLeaveBalanceRequestDTO;
import com.neg.technology.human.resource.dto.entity.LeaveBalanceEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateLeaveBalanceRequestDTO;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.entity.LeaveBalance;
import com.neg.technology.human.resource.entity.LeaveType;

public class LeaveBalanceMapper {
    public static LeaveBalanceEntityDTO toDTO(LeaveBalance leaveBalance) {
        return LeaveBalanceEntityDTO.builder()
                .id(leaveBalance.getId())
                .employeeFirstName(leaveBalance.getEmployee().getPerson().getFirstName())
                .employeeLastName(leaveBalance.getEmployee().getPerson().getLastName())
                .leaveTypeName(leaveBalance.getLeaveType().getName())
                .leaveTypeBorrowableLimit(leaveBalance.getLeaveType().getBorrowableLimit())
                .leaveTypeIsUnpaid(leaveBalance.getLeaveType().getIsUnpaid())
                .date(leaveBalance.getDate())
                .amount(leaveBalance.getAmount())
                .build();
    }

    public static LeaveBalance toEntity(CreateLeaveBalanceRequestDTO dto, Employee employee, LeaveType leaveType) {
        return LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .date(dto.getDate())
                .amount(dto.getAmount())
                .build();
    }

    public static void updateEntity(LeaveBalance existing, UpdateLeaveBalanceRequestDTO dto, Employee employee, LeaveType leaveType) {
        if (employee != null) {
            existing.setEmployee(employee);
        }
        if (leaveType != null) {
            existing.setLeaveType(leaveType);
        }
        if (dto.getDate() != null) {
            existing.setDate(dto.getDate());
        }
        if (dto.getAmount() != null) {
            existing.setAmount(dto.getAmount());
        }
    }
}
