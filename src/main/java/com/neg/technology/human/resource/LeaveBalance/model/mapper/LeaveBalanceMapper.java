package com.neg.technology.human.resource.LeaveBalance.model.mapper;

import com.neg.technology.human.resource.LeaveBalance.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.LeaveBalance.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.LeaveBalance.model.entity.LeaveBalance;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;

public class LeaveBalanceMapper {
    public static LeaveBalanceResponse toDTO(LeaveBalance leaveBalance) {
        return LeaveBalanceResponse.builder()
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

    public static LeaveBalance toEntity(CreateLeaveBalanceRequest dto, Employee employee, LeaveType leaveType) {
        return LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .date(dto.getDate())
                .amount(dto.getAmount())
                .build();
    }

    public static void updateEntity(LeaveBalance existing, UpdateLeaveBalanceRequest dto, Employee employee, LeaveType leaveType) {
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
