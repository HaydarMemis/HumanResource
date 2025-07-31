package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.CreateLeaveBalanceDTO;
import com.neg.hr.human.resource.dto.LeaveBalanceDTO;
import com.neg.hr.human.resource.dto.UpdateLeaveBalanceDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveBalance;
import com.neg.hr.human.resource.entity.LeaveType;

public class LeaveBalanceMapper {
    public static LeaveBalanceDTO toDTO(LeaveBalance leaveBalance) {
        return LeaveBalanceDTO.builder()
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

    public static LeaveBalance toEntity(CreateLeaveBalanceDTO dto, Employee employee, LeaveType leaveType) {
        return LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .date(dto.getDate())
                .amount(dto.getAmount())
                .build();
    }

    public static void updateEntity(LeaveBalance existing, UpdateLeaveBalanceDTO dto, Employee employee, LeaveType leaveType) {
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
