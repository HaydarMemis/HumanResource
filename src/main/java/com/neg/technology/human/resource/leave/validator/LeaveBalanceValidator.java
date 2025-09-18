package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.leave.model.enums.Gender;
import com.neg.technology.human.resource.exception.LeaveBalanceExceededException;
import com.neg.technology.human.resource.exception.InvalidLeaveRequestException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class LeaveBalanceValidator {

    private static final Logger log = LoggerFactory.getLogger(LeaveBalanceValidator.class);

    /** Türkçe karakterleri normalize etme yardımcı metodu */
    private static String normalizeTurkish(String input) {
        if (input == null) return null;
        return input
                .replace("İ", "i")
                .replace("I", "i")
                .replace("ı", "i")
                .replace("Ğ", "g")
                .replace("ğ", "g")
                .replace("Ü", "u")
                .replace("ü", "u")
                .replace("Ş", "s")
                .replace("ş", "s")
                .replace("Ö", "o")
                .replace("ö", "o")
                .replace("Ç", "c")
                .replace("ç", "c")
                .trim()
                .toLowerCase();
    }

    /**
     * Çalışanın yıllık izin hakkını kıdemine göre hesaplar
     */
    public static BigDecimal calculateAnnualLeave(Employee employee) {
        LocalDateTime startDateTime = employee.getEmploymentStartDate();
        log.info("Calculating annual leave for employee id={} | employmentStartDate={}", employee.getId(), startDateTime);

        if (startDateTime == null) {
            log.warn("Employment start date is null, returning 0");
            return BigDecimal.ZERO;
        }

        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate today = LocalDate.now();

        long yearsOfService = ChronoUnit.YEARS.between(startDate, today);
        LocalDate anniversary = startDate.plusYears(yearsOfService);
        if (anniversary.isAfter(today)) yearsOfService--;

        log.info("Years of service: {}", yearsOfService);

        if (yearsOfService >= 1 && yearsOfService <= 5) return BigDecimal.valueOf(14);
        else if (yearsOfService >= 6 && yearsOfService <= 15) return BigDecimal.valueOf(20);
        else if (yearsOfService > 15) return BigDecimal.valueOf(26);

        return BigDecimal.ZERO;
    }

    /**
     * Kullanıcının talep ettiği izin miktarının bakiyeyi aşmadığını kontrol eder
     */
    public void hasEnoughBalance(BigDecimal totalBalance, BigDecimal requestedDays) {
        log.info("Checking if enough balance exists: totalBalance={}, requestedDays={}", totalBalance, requestedDays);
        if (requestedDays == null || requestedDays.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Requested days must be greater than zero.");
        }
        if (totalBalance == null || totalBalance.compareTo(requestedDays) < 0) {
            throw new LeaveBalanceExceededException(
                    "Yetersiz izin bakiyesi. Mevcut bakiye: " + totalBalance + ", İstenen: " + requestedDays
            );
        }
        log.info("Enough balance available.");
    }

    /**
     * Reactive olarak yıllık izin hakkını hesaplar
     */
    public Mono<BigDecimal> getAnnualLeaveAllowanceReactive(Employee employee, LeaveType leaveType) {
        return Mono.fromSupplier(() -> {
            String leaveTypeNameRaw = leaveType.getName();
            String leaveTypeName = normalizeTurkish(leaveTypeNameRaw);
            Gender gender = employee.getPerson().getGender();

            log.info("Calculating allowance for leaveType='{}' (normalized='{}') | Employee Gender={}", leaveTypeNameRaw, leaveTypeName, gender);

            switch (leaveTypeName) {
                case "paternity leave":
                    if (gender != Gender.MALE) {
                        throw new InvalidLeaveRequestException("Babalık izni sadece erkek çalışanlar içindir.");
                    }
                    return BigDecimal.valueOf(5);
                case "maternity leave":
                    if (gender != Gender.FEMALE) {
                        throw new InvalidLeaveRequestException("Annelik izni sadece kadın çalışanlar içindir.");
                    }
                    return BigDecimal.valueOf(112);
                case "yillik izin":
                    BigDecimal allowance = calculateAnnualLeave(employee);
                    log.info("Calculated annual allowance: {}", allowance);
                    return allowance;
                default:
                    log.warn("Unknown leave type: '{}', returning 0", leaveTypeNameRaw);
                    return BigDecimal.ZERO;
            }
        }).doOnSuccess(allowance -> log.info("Final calculated allowance: {}", allowance));
    }

    /**
     * Var olan izin bakiyesine ekleme yapılırken limit kontrolü
     */
    public void validateLeaveAddition(
            LeaveBalance existingBalance,
            LeaveType leaveType,
            BigDecimal amountToAdd,
            Employee employee) {

        log.info("Validating leave addition for employee id={}", employee.getId());

        BigDecimal annualAllowance = getAnnualLeaveAllowanceReactive(employee, leaveType).block();
        BigDecimal currentAmount = existingBalance != null ? existingBalance.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal newAmount = currentAmount.add(amountToAdd);

        log.info("Current amount={}, Adding={}, AnnualAllowance={}", currentAmount, amountToAdd, annualAllowance);

        if (newAmount.compareTo(annualAllowance) > 0) {
            throw new LeaveBalanceExceededException(
                    "İzin eklenemiyor. Yıllık izin limitini aşıyor. Yıllık izin hakkı: "
                            + annualAllowance + ", Mevcut izin: " + currentAmount + ", Eklenmek istenen: " + amountToAdd
            );
        }
    }

    /**
     * Yeni izin oluşturulurken limit kontrolü
     */
    public void validateLeaveCreation(BigDecimal requestedAmount, Employee employee, LeaveType leaveType) {
        log.info("Validating leave creation for employee id={}", employee.getId());

        BigDecimal annualAllowance = getAnnualLeaveAllowanceReactive(employee, leaveType).block();

        log.info("RequestedAmount={}, AnnualAllowance={}", requestedAmount, annualAllowance);

        if (requestedAmount.compareTo(annualAllowance) > 0) {
            throw new LeaveBalanceExceededException(
                    "İzin eklenemiyor. Yıllık izin limitini aşıyor. Yıllık izin hakkı: "
                            + annualAllowance + ", Eklenmek istenen: " + requestedAmount
            );
        }
    }
}
