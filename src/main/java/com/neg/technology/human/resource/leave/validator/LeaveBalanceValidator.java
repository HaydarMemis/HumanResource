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

        BigDecimal maxNegative = BigDecimal.valueOf(-5); // izin negatif limiti
        BigDecimal projectedBalance = totalBalance.subtract(requestedDays);

        if (projectedBalance.compareTo(maxNegative) < 0) {
            throw new LeaveBalanceExceededException(
                    "Yetersiz izin bakiyesi. Mevcut bakiye: " + totalBalance +
                            ", İstenen: " + requestedDays +
                            ". Maximum negative allowed: -5 gün.");
        }
    }

    /**
     * Çalışanın yıllık izin hakkını leave tipi ve hizmet yılına göre hesaplar
     */
    public BigDecimal getAnnualLeaveAllowance(Employee employee, LeaveType leaveType) {
        if (!Boolean.TRUE.equals(leaveType.getIsAnnual())) {
            return BigDecimal.ZERO; // Yıllık izin değilse 0 döner
        }

        if (employee.getEmploymentStartDate() == null) {
            return BigDecimal.ZERO;
        }

        LocalDate hireDate = employee.getEmploymentStartDate().toLocalDate();
        LocalDate today = LocalDate.now();
        Period period = Period.between(hireDate, today);
        int yearsOfService = period.getYears();

        // 1 yıldan az çalışana izin 0 gün
        if (yearsOfService < 1) {
            return BigDecimal.ZERO;
        }

        // Hizmet yılına göre izin günleri
        if (yearsOfService >= 1 && yearsOfService <= 5) {
            return BigDecimal.valueOf(14);
        } else if (yearsOfService >= 6 && yearsOfService <= 15) {
            return BigDecimal.valueOf(20);
        } else if (yearsOfService > 15) {
            return BigDecimal.valueOf(26);
        }
        return BigDecimal.ZERO;
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
                            ", Mevcut izin: " + currentTotal + ", Eklenmek istenen: " + amountToAdd);
        }
    }

    /**
     * Yeni izin oluşturma talebini validate eder
     */
    public void validateLeaveCreation(BigDecimal requestedAmount, Employee employee, LeaveType leaveType) {
        Gender employeeGender = employee.getPerson().getGender();

        // --- Cinsiyet bazlı validasyon ---
        if (leaveType.getGenderRequired() != null) {
            if (!leaveType.getGenderRequired().equals(employeeGender)) {
                throw new InvalidLeaveRequestException(
                        leaveType.getName() + " sadece " + leaveType.getGenderRequired()
                                + " çalışanlar için geçerlidir.");
            }
        }

        // --- Yıllık izin mi kontrolü ---
        if (Boolean.TRUE.equals(leaveType.getIsAnnual())) {
            // yıllık izin -> kıdeme göre max gün
            BigDecimal annualAllowance = getAnnualLeaveAllowance(employee, leaveType);
            if (requestedAmount.compareTo(annualAllowance) > 0) {
                throw new LeaveBalanceExceededException(
                        "Yıllık izin limiti aşıldı. Hakkınız: " + annualAllowance + ", Talep edilen: " + requestedAmount
                                + " gün.");
            }
        } else {
            // diğer izinler -> leaveType maxDays veya defaultDays
            BigDecimal maxDays = leaveType.getMaxDays() != null
                    ? BigDecimal.valueOf(leaveType.getMaxDays())
                    : (leaveType.getDefaultDays() != null ? BigDecimal.valueOf(leaveType.getDefaultDays())
                            : BigDecimal.ZERO);

            if (requestedAmount.compareTo(maxDays) > 0) {
                throw new LeaveBalanceExceededException(
                        leaveType.getName() + " için talep edilen izin (" + requestedAmount +
                                " gün) maksimum izin süresini (" + maxDays + " gün) aşıyor.");
            }
        }
    }

    /**
     * Kullanıcının istediği izin miktarını izin hakkı ile sınırlar
     */
    public BigDecimal getCappedLeaveAmount(BigDecimal requestedAmount, Employee employee, LeaveType leaveType) {
        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal allowedAmount;
        if (Boolean.TRUE.equals(leaveType.getIsAnnual())) {
            allowedAmount = getAnnualLeaveAllowance(employee, leaveType);
        } else {
            allowedAmount = leaveType.getMaxDays() != null
                    ? BigDecimal.valueOf(leaveType.getMaxDays())
                    : (leaveType.getDefaultDays() != null ? BigDecimal.valueOf(leaveType.getDefaultDays())
                            : requestedAmount);
        }

        return requestedAmount.compareTo(allowedAmount) > 0 ? allowedAmount : requestedAmount;
    }

}