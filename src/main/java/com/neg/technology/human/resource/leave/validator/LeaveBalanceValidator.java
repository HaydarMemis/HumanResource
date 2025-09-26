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
import java.util.Locale;
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
                .map(b -> Optional.ofNullable(b.getTotalDays()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal usedDays = balances.stream()
                .map(b -> Optional.ofNullable(b.getUsedDays()).orElse(BigDecimal.ZERO))
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
        BigDecimal available = Optional.ofNullable(totalBalance).orElse(BigDecimal.ZERO);
        if (available.compareTo(requestedDays) < 0) {
            throw new LeaveBalanceExceededException(
                    "Yetersiz izin bakiyesi. Mevcut bakiye: " + available + ", İstenen: " + requestedDays
            );
        }
    }

    /**
     * Çalışanın yıllık izin hakkını leave tipi ve hizmet yılına göre hesaplar
     */
    public BigDecimal getAnnualLeaveAllowance(Employee employee, LeaveType leaveType) {
        if (employee == null || leaveType == null) return BigDecimal.ZERO;

        String rawName = Optional.ofNullable(leaveType.getName()).orElse("").trim().toLowerCase(Locale.ROOT);
        LocalDateTime startDateTime = employee.getEmploymentStartDate();
        Gender gender = employee.getPerson() != null ? employee.getPerson().getGender() : null;

        // Paternity
        if (rawName.contains("paternity") || rawName.contains("babalık") || rawName.contains("paternity leave")) {
            if (gender != Gender.MALE) {
                throw new InvalidLeaveRequestException("Babalık izni sadece erkek çalışanlar içindir.");
            }
            return BigDecimal.valueOf(5);
        }

        // Maternity
        if (rawName.contains("maternity") || rawName.contains("annelik") || rawName.contains("maternity leave")) {
            if (gender != Gender.FEMALE) {
                throw new InvalidLeaveRequestException("Annelik izni sadece kadın çalışanlar içindir.");
            }
            return BigDecimal.valueOf(112);
        }

        // Annual / Yıllık
        if (rawName.contains("annual") || rawName.contains("yıllık") || rawName.contains("yillik") || rawName.contains("annual leave")) {
            if (startDateTime == null) return BigDecimal.ZERO;
            LocalDate startDate = startDateTime.toLocalDate();
            Period period = Period.between(startDate, LocalDate.now());
            int yearsOfService = period.getYears();

            if (yearsOfService >= 1 && yearsOfService <= 5) {
                return BigDecimal.valueOf(14);
            } else if (yearsOfService >= 6 && yearsOfService <= 15) {
                return BigDecimal.valueOf(20);
            } else if (yearsOfService > 15) {
                return BigDecimal.valueOf(26);
            }
            return BigDecimal.ZERO;
        }

        // Diğer leave tipleri için entity'den max veya default var ise onu kullan (LeaveType yapısına bağlı).
        // Burada genel kural: özel tipler için servis tarafında kontrol yapılmalı. Burayı 0 döndürüyoruz.
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

        if (amountToAdd == null || amountToAdd.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Added amount must be greater than zero.");
        }
        if (leaveType == null || employee == null) {
            throw new IllegalArgumentException("LeaveType and Employee must not be null for validation.");
        }

        String rawName = Optional.ofNullable(leaveType.getName()).orElse("").trim().toLowerCase(Locale.ROOT);
        Gender gender = employee.getPerson() != null ? employee.getPerson().getGender() : null;

        if ((rawName.contains("maternity") || rawName.contains("annelik")) && gender != Gender.FEMALE) {
            throw new InvalidLeaveRequestException("Maternity leave is only for female employees.");
        }
        if ((rawName.contains("paternity") || rawName.contains("babalık")) && gender != Gender.MALE) {
            throw new InvalidLeaveRequestException("Paternity leave is only for male employees.");
        }

        BigDecimal currentTotal = Optional.ofNullable(existingBalance)
                .map(LeaveBalance::getTotalDays)
                .orElse(BigDecimal.ZERO);

        BigDecimal newTotal = currentTotal.add(amountToAdd);

        BigDecimal annualAllowance = getAnnualLeaveAllowance(employee, leaveType);
        // Eğer allowance 0 ise bu tip için yıllık üst sınır yok demektir (veya ayrı politikaya tabi). Sadece >0 ise kontrol et.
        if (annualAllowance.compareTo(BigDecimal.ZERO) > 0 && newTotal.compareTo(annualAllowance) > 0) {
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
        if (requestedAmount == null || requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Requested amount must be greater than zero.");
        }
        if (employee == null || leaveType == null) {
            throw new IllegalArgumentException("Employee and LeaveType are required.");
        }

        BigDecimal annualAllowance = getAnnualLeaveAllowance(employee, leaveType);
        if (annualAllowance.compareTo(BigDecimal.ZERO) > 0 && requestedAmount.compareTo(annualAllowance) > 0) {
            throw new LeaveBalanceExceededException(
                    "İzin eklenemiyor. Yıllık izin limitini aşıyor. Yıllık izin hakkı: " + annualAllowance +
                            ", Eklenmek istenen: " + requestedAmount
            );
        }
    }
}
