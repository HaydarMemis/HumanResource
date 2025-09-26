package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.leave.model.enums.Gender;
import com.neg.technology.human.resource.exception.LeaveBalanceExceededException;
import com.neg.technology.human.resource.exception.InvalidLeaveRequestException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class LeaveBalanceValidator {

    public BigDecimal calculateTotalBalance(List<LeaveBalance> balances) {
        if (balances == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalAmount = balances.stream()
                .map(LeaveBalance::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalUsedDays = balances.stream()
                .mapToInt(LeaveBalance::getUsedDays)
                .sum();

        return totalAmount.subtract(BigDecimal.valueOf(totalUsedDays));
    }


    public void hasEnoughBalance(BigDecimal totalBalance, BigDecimal requestedDays) {
        if (requestedDays == null || requestedDays.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Requested days must be greater than zero.");
        }
        if (totalBalance == null || totalBalance.compareTo(requestedDays) < 0) {
            throw new LeaveBalanceExceededException("Yetersiz izin bakiyesi. Mevcut bakiye: " + totalBalance + ", İstenen: " + requestedDays);
        }
    }

    public BigDecimal getAnnualLeaveAllowance(Employee employee, LeaveType leaveType) {
        String leaveTypeName = leaveType.getName().trim().toLowerCase();
        Gender gender = employee.getPerson().getGender();

        switch (leaveTypeName) {
            case "paternity leave": //enum tanimla
                if (gender != Gender.MALE) {
                    throw new InvalidLeaveRequestException("Babalık izni sadece erkek çalışanlar içindir.");
                }
                return new BigDecimal(5);
            case "maternity leave":
                if (gender != Gender.FEMALE) {
                    throw new InvalidLeaveRequestException("Annelik izni sadece kadın çalışanlar içindir.");
                }
                return new BigDecimal(112);
            case "yıllık izin":
                LocalDateTime startDateTime = employee.getEmploymentStartDate();
                if (startDateTime == null) {
                    return BigDecimal.ZERO;
                }
                LocalDate startDate = startDateTime.toLocalDate();
                Period period = Period.between(startDate, LocalDate.now());
                int yearsOfService = period.getYears();

                if (yearsOfService >= 1 && yearsOfService <= 5) {
                    return new BigDecimal(14);
                } else if (yearsOfService >= 6 && yearsOfService <= 15) {
                    return new BigDecimal(20);
                } else if (yearsOfService > 15) {
                    return new BigDecimal(26);
                }
                return BigDecimal.ZERO;
            default:
                return BigDecimal.ZERO;
        }
    }

    public void validateLeaveAddition(
            LeaveBalance existingBalance,
            LeaveType leaveType,
            BigDecimal amountToAdd,
            Employee employee) {

        BigDecimal annualAllowance = getAnnualLeaveAllowance(employee, leaveType);
        String leaveTypeName = leaveType.getName().trim().toLowerCase();
        Gender gender = employee.getPerson().getGender();

        if ("maternity leave".equals(leaveTypeName) && gender != Gender.FEMALE) {
            throw new InvalidLeaveRequestException("Maternity leave is only for female employees.");
        }
        if ("paternity leave".equals(leaveTypeName) && gender != Gender.MALE) {
            throw new InvalidLeaveRequestException("Paternity leave is only for male employees.");
        }

        BigDecimal currentAmount = Optional.ofNullable(existingBalance)
                .map(LeaveBalance::getAmount)
                .orElse(BigDecimal.ZERO);
        BigDecimal newAmount = currentAmount.add(amountToAdd);

        if (newAmount.compareTo(annualAllowance) > 0) {
            throw new LeaveBalanceExceededException(
                    "İzin eklenemiyor. Yıllık izin limitini aşıyor. Yıllık izin hakkı: " + annualAllowance + ", Mevcut izin: " + currentAmount + ", Eklenmek istenen: " + amountToAdd
            );
        }
    }

    public void validateLeaveCreation(BigDecimal requestedAmount, Employee employee, LeaveType leaveType) {
        BigDecimal annualAllowance = getAnnualLeaveAllowance(employee, leaveType);
        if (requestedAmount.compareTo(annualAllowance) > 0) {
            throw new LeaveBalanceExceededException(
                    "İzin eklenemiyor. Yıllık izin limitini aşıyor. Yıllık izin hakkı: " + annualAllowance + ", Eklenmek istenen: " + requestedAmount
            );
        }
    }
}