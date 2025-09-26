package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.exception.LeaveBalanceExceededException;
import com.neg.technology.human.resource.exception.InvalidLeaveRequestException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.person.model.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LeaveBalanceValidator {

    /**
     * Birden fazla bakiye varsa toplam kullanılabilir bakiyeyi hesaplar
     */
    public BigDecimal calculateTotalBalance(List<LeaveBalance> balances) {
        if (balances == null || balances.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalDays = balances.stream()
                .map(LeaveBalance::getTotalDays)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal usedDays = balances.stream()
                .map(LeaveBalance::getUsedDays)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalDays.subtract(usedDays);
    }

    /**
     * Yeterli bakiye varsa izin talebine izin verir, yoksa exception fırlatır
     */
    public void hasEnoughBalance(BigDecimal totalBalance, BigDecimal requestedDays) {
        if (requestedDays == null || requestedDays.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Requested days must be greater than zero.");
        }
        if (totalBalance == null || totalBalance.compareTo(requestedDays) < 0) {
            throw new LeaveBalanceExceededException(
                    "Yetersiz izin bakiyesi. Mevcut bakiye: " + totalBalance + ", İstenen: " + requestedDays
            );
        }
    }

    /**
     * Çalışanın yıllık izin hakkını leave tipi ve hizmet yılına göre hesaplar
     */
    public BigDecimal getAnnualLeaveAllowance(Employee employee, LeaveType leaveType) {
        String leaveTypeName = leaveType.getName().trim().toLowerCase();
        Gender gender = employee.getPerson().getGender();

        switch (leaveTypeName) {
            case "paternity leave":
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

    /**
     * Var olan bakiyeye izin ekleme işlemini validate eder
     */
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

        BigDecimal currentTotal = Optional.ofNullable(existingBalance)
                .map(LeaveBalance::getTotalDays)
                .orElse(BigDecimal.ZERO);

        BigDecimal newTotal = currentTotal.add(amountToAdd);

        if (newTotal.compareTo(annualAllowance) > 0) {
            throw new LeaveBalanceExceededException(
                    "İzin eklenemiyor. Yıllık izin limitini aşıyor. Yıllık izin hakkı: " + annualAllowance +
                    ", Mevcut izin: " + currentTotal + ", Eklenmek istenen: " + amountToAdd
            );
        }
    }

    /**
     * Yeni izin oluşturma talebini validate eder
     */
    public void validateLeaveCreation(BigDecimal requestedAmount, Employee employee, LeaveType leaveType) {
        BigDecimal annualAllowance = getAnnualLeaveAllowance(employee, leaveType);

        if (requestedAmount.compareTo(annualAllowance) > 0) {
            throw new LeaveBalanceExceededException(
                    "İzin eklenemiyor. Yıllık izin limitini aşıyor. Yıllık izin hakkı: " + annualAllowance +
                    ", Eklenmek istenen: " + requestedAmount
            );
        }
    }
}
