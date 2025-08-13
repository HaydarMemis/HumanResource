package com.neg.technology.human.resource.LeaveBalance.model.mapper;

import com.neg.technology.human.resource.LeaveBalance.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.LeaveBalance.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.LeaveBalance.model.entity.LeaveBalance;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LeaveBalanceMapper {

    public LeaveBalanceResponse toResponse(LeaveBalance leaveBalance) {
        if (leaveBalance == null) {
            return null;
        }
        return LeaveBalanceResponse.builder()
                .id(leaveBalance.getId())
                .employeeFirstName(
                        leaveBalance.getEmployee() != null && leaveBalance.getEmployee().getPerson() != null
                                ? leaveBalance.getEmployee().getPerson().getFirstName()
                                : null
                )
                .employeeLastName(
                        leaveBalance.getEmployee() != null && leaveBalance.getEmployee().getPerson() != null
                                ? leaveBalance.getEmployee().getPerson().getLastName()
                                : null
                )
                .leaveTypeName(leaveBalance.getLeaveType() != null ? leaveBalance.getLeaveType().getName() : null)
                .leaveTypeBorrowableLimit(leaveBalance.getLeaveType() != null ? leaveBalance.getLeaveType().getBorrowableLimit() : null)
                .leaveTypeIsUnpaid(leaveBalance.getLeaveType() != null ? leaveBalance.getLeaveType().getIsUnpaid() : null)
                .date(leaveBalance.getDate())
                .amount(leaveBalance.getAmount())
                .build();
    }

    public LeaveBalanceResponseList toResponseList(List<LeaveBalance> leaveBalances) {
        List<LeaveBalanceResponse> responses = leaveBalances.stream()
                .map(this::toResponse)
                .toList();
        return new LeaveBalanceResponseList(responses);
    }

    public LeaveBalance toEntity(CreateLeaveBalanceRequest dto, Employee employee, LeaveType leaveType) {
        if (dto == null) {
            return null;
        }
        return LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .date(dto.getDate())
                .amount(dto.getAmount())
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
        if (dto.getDate() != null) {
            existing.setDate(dto.getDate());
        }
        if (dto.getAmount() != null) {
            existing.setAmount(dto.getAmount());
        }
    }
}
