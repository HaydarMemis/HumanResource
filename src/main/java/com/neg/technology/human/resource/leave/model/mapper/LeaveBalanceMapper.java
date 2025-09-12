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
import java.math.BigDecimal;

@Component
public class LeaveBalanceMapper {

    public LeaveBalanceResponse toResponse(LeaveBalance leaveBalance, List<LeaveBalance> allBalancesOfType) {
        if (leaveBalance == null) {
            return null;
        }

        // Tüm yıllardaki toplam izin hakkı ve kullanılan izin
        BigDecimal totalAllowance = allBalancesOfType.stream()
                .map(lb -> lb.getEarnedDays() != null ? BigDecimal.valueOf(lb.getEarnedDays()) : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalUsed = allBalancesOfType.stream()
                .mapToInt(lb -> lb.getUsedDays())
                .sum();

        int usedThisYear = leaveBalance.getUsedDays();
        BigDecimal remaining = totalAllowance.subtract(BigDecimal.valueOf(totalUsed));

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
                .date(leaveBalance.getYear())
                .amount(BigDecimal.valueOf(leaveBalance.getEarnedDays()))
                .totalAllowance(totalAllowance)
                .totalUsed(BigDecimal.valueOf(totalUsed))
                .remaining(remaining)
                .usedThisYear(usedThisYear)
                .build();
    }

    public LeaveBalanceResponseList toResponseList(List<LeaveBalance> leaveBalances) {
        if (leaveBalances == null || leaveBalances.isEmpty()) {
            return new LeaveBalanceResponseList(List.of());
        }

        // Tüm yıllardaki balance listesi (aynı leaveType ve employee için)
        List<LeaveBalanceResponse> responses = leaveBalances.stream()
                .map(lb -> toResponse(lb, leaveBalances))
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
                .year(dto.getDate())
                .earnedDays(dto.getAmount() != null ? dto.getAmount().intValue() : 0)
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
            existing.setYear(dto.getDate());
        }
        if (dto.getAmount() != null) {
            existing.setEarnedDays(dto.getAmount().intValue());
        }
    }
}