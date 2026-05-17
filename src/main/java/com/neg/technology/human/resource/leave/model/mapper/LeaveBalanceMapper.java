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

@Component
public class LeaveBalanceMapper {

    // Entity -> Response
    public LeaveBalanceResponse toResponse(LeaveBalance leaveBalance) {
        if (leaveBalance == null) {
            return null;
        }
        return LeaveBalanceResponse.builder()
                .id(leaveBalance.getId())
                .employeeFirstName(
                        leaveBalance.getEmployee() != null && leaveBalance.getEmployee().getPerson() != null
                                ? leaveBalance.getEmployee().getPerson().getFirstName()
                                : null)
                .employeeLastName(
                        leaveBalance.getEmployee() != null && leaveBalance.getEmployee().getPerson() != null
                                ? leaveBalance.getEmployee().getPerson().getLastName()
                                : null)
                .leaveTypeName(leaveBalance.getLeaveType() != null ? leaveBalance.getLeaveType().getName() : null)
                .year(leaveBalance.getYear())
                .totalDays(leaveBalance.getTotalDays())
                .usedDays(leaveBalance.getUsedDays())
                .availableBalance(leaveBalance.getAvailableBalance())
                .build();
    }

    // Liste dönüşümü
    public LeaveBalanceResponseList toResponseList(List<LeaveBalance> leaveBalances) {
        List<LeaveBalanceResponse> responses = leaveBalances.stream()
                .map(this::toResponse)
                .toList();
        return new LeaveBalanceResponseList(responses);
    }

    // Request -> Entity (create)
    public LeaveBalance toEntity(CreateLeaveBalanceRequest dto, Employee employee, LeaveType leaveType) {
        if (dto == null) {
            return null;
        }
        return LeaveBalance.builder()
                .employee(employee)
                .leaveType(leaveType)
                .year(dto.getYear())
                .totalDays(dto.getTotalDays())
                .build();
    }

    // Request -> Entity (update)
    public void updateEntity(LeaveBalance existing, UpdateLeaveBalanceRequest dto, Employee employee,
            LeaveType leaveType) {
        if (existing == null || dto == null) {
            return;
        }
        if (employee != null) {
            existing.setEmployee(employee);
        }
        if (leaveType != null) {
            existing.setLeaveType(leaveType);
        }
        if (dto.getYear() != null) {
            existing.setYear(dto.getYear());
        }
        if (dto.getTotalDays() != null) {
            existing.setTotalDays(dto.getTotalDays());
        }
        if (dto.getUsedDays() != null) {
            existing.setUsedDays(dto.getUsedDays());
        }
    }
}
