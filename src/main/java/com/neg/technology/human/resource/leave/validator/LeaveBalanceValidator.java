package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.exception.InvalidLeaveRequestException;
import com.neg.technology.human.resource.exception.LeaveBalanceExceededException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.person.model.enums.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeaveBalanceValidator {

    /**
     * Birden fazla bakiye varsa toplam kullanılabilir bakiyeyi hesaplar.
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
     * Yeterli bakiye varsa izin talebine izin verir, yoksa hata fırlatır.
     */
    public void hasEnoughBalance(BigDecimal totalBalance, BigDecimal requestedDays) {
        if (requestedDays == null || requestedDays.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Talep edilen izin günü 0’dan büyük olmalıdır.");
        }

        BigDecimal maxNegative = BigDecimal.valueOf(-5);
        BigDecimal projectedBalance = totalBalance.subtract(requestedDays);

        if (projectedBalance.compareTo(maxNegative) < 0) {
            throw LeaveBalanceExceededException.custom(
                    "Yetersiz izin bakiyesi. Mevcut bakiye: " + totalBalance +
                            " gün, Talep edilen: " + requestedDays +
                            " gün. En fazla -5 gün eksiye düşülebilir."
            );
        }
    }

    public BigDecimal getAnnualLeaveAllowance(Employee employee, LeaveType leaveType) {
        if (!Boolean.TRUE.equals(leaveType.getIsAnnual())) {
            return BigDecimal.ZERO;
        }

        if (employee.getEmploymentStartDate() == null) {
            return BigDecimal.ZERO;
        }

        LocalDate hireDate = employee.getEmploymentStartDate().toLocalDate();
        LocalDate today = LocalDate.now();
        int yearsOfService = Period.between(hireDate, today).getYears();

        if (yearsOfService < 1) {
            return BigDecimal.ZERO;
        } else if (yearsOfService <= 5) {
            return BigDecimal.valueOf(14);
        } else if (yearsOfService <= 15) {
            return BigDecimal.valueOf(20);
        } else {
            return BigDecimal.valueOf(26);
        }
    }

    public void validateLeaveAddition(LeaveBalance existingBalance,
                                      LeaveType leaveType,
                                      BigDecimal amountToAdd,
                                      Employee employee) {

        BigDecimal annualAllowance = getAnnualLeaveAllowance(employee, leaveType);
        String leaveTypeName = leaveType.getName().trim().toLowerCase();
        Gender gender = employee.getPerson().getGender();

        if ("maternity leave".equals(leaveTypeName) && gender != Gender.FEMALE) {
            throw InvalidLeaveRequestException.invalidRequest("Doğum izni sadece kadın çalışanlar için geçerlidir.");
        }

        if ("paternity leave".equals(leaveTypeName) && gender != Gender.MALE) {
            throw InvalidLeaveRequestException.invalidRequest("Babalık izni sadece erkek çalışanlar için geçerlidir.");
        }

        BigDecimal currentTotal = existingBalance != null
                ? existingBalance.getTotalDays()
                : BigDecimal.ZERO;

        BigDecimal newTotal = currentTotal.add(amountToAdd);

        if (newTotal.compareTo(annualAllowance) > 0) {
            throw LeaveBalanceExceededException.custom(
                    "İzin eklenemiyor. Yıllık izin limitini aşıyor. Hakkınız: " + annualAllowance +
                            " gün, Mevcut izin: " + currentTotal +
                            " gün, Eklenmek istenen: " + amountToAdd + " gün."
            );
        }
    }

    public void validateLeaveCreation(BigDecimal requestedAmount,
                                      Employee employee,
                                      LeaveType leaveType) {

        Gender employeeGender = employee.getPerson().getGender();
        Gender requiredGender = leaveType.getGenderRequired();

        if (requiredGender != null && requiredGender != Gender.OTHER) {
            if (requiredGender != employeeGender) {
                throw InvalidLeaveRequestException.invalidRequest(
                        "Bu izin türü (" + leaveType.getName() +
                                ") çalışanın cinsiyeti (" + employeeGender +
                                ") ile uyumlu değildir."
                );
            }
        }

        if (Boolean.TRUE.equals(leaveType.getIsAnnual())) {
            BigDecimal annualAllowance = getAnnualLeaveAllowance(employee, leaveType);
            if (requestedAmount.compareTo(annualAllowance) > 0) {
                throw LeaveBalanceExceededException.custom(
                        "Yıllık izin limiti aşıldı. Hakkınız: " + annualAllowance +
                                " gün, Talep edilen: " + requestedAmount + " gün."
                );
            }
        } else {
            BigDecimal maxDays = leaveType.getMaxDays() != null
                    ? BigDecimal.valueOf(leaveType.getMaxDays())
                    : (leaveType.getDefaultDays() != null
                        ? BigDecimal.valueOf(leaveType.getDefaultDays())
                        : BigDecimal.ZERO);

            if (requestedAmount.compareTo(maxDays) > 0) {
                throw LeaveBalanceExceededException.custom(
                        leaveType.getName() + " için talep edilen (" + requestedAmount +
                                " gün) izin, maksimum süreyi (" + maxDays + " gün) aşıyor."
                );
            }
        }
    }

    public BigDecimal getCappedLeaveAmount(BigDecimal requestedAmount,
                                           Employee employee,
                                           LeaveType leaveType) {

        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal allowedAmount;

        if (Boolean.TRUE.equals(leaveType.getIsAnnual())) {
            allowedAmount = getAnnualLeaveAllowance(employee, leaveType);
        } else {
            allowedAmount = leaveType.getMaxDays() != null
                    ? BigDecimal.valueOf(leaveType.getMaxDays())
                    : (leaveType.getDefaultDays() != null
                        ? BigDecimal.valueOf(leaveType.getDefaultDays())
                        : requestedAmount);
        }

        return requestedAmount.compareTo(allowedAmount) > 0 ? allowedAmount : requestedAmount;
    }
}
