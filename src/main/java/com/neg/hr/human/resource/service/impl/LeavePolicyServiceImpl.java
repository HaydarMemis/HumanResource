package com.neg.hr.human.resource.service.impl;

import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.service.LeavePolicyService;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Set;

@Service
public class LeavePolicyServiceImpl implements LeavePolicyService {

    // Örnek resmi tatiller (gerçek uygulamada veritabanı/tablodan çekilmeli)
    private static final Set<LocalDate> OFFICIAL_HOLIDAYS = Set.of(
            LocalDate.of(2025, Month.JANUARY, 1),
            LocalDate.of(2025, Month.APRIL, 23),
            LocalDate.of(2025, Month.MAY, 1),
            LocalDate.of(2025, Month.MAY, 19),
            LocalDate.of(2025, Month.JULY, 15),
            LocalDate.of(2025, Month.AUGUST, 30),
            LocalDate.of(2025, Month.OCTOBER, 29)
    );

    private LocalDate getEmploymentStartDate(Employee employee) {
        LocalDateTime dt = employee.getEmploymentStartDate();
        return dt != null ? dt.toLocalDate() : null;
    }

    private LocalDate getBirthDate(Employee employee) {
        return employee.getPerson() != null ? employee.getPerson().getBirthDate() : null;
    }

    private String getGender(Employee employee) {
        return employee.getPerson() != null ? employee.getPerson().getGender() : null;
    }

    private int calculateYearsBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return Period.between(startDate, endDate).getYears();
    }

    @Override
    public int calculateAnnualLeaveDays(Employee employee) {
        LocalDate startDate = getEmploymentStartDate(employee);
        int yearsWorked = calculateYearsBetween(startDate, LocalDate.now());

        if (yearsWorked < 1) return 0;
        if (yearsWorked < 5) return 14;
        if (yearsWorked < 15) return 20;
        return 26;
    }

    @Override
    public int calculateAgeBasedLeaveBonus(Employee employee) {
        LocalDate birthDate = getBirthDate(employee);
        if (birthDate == null) return 0;

        int age = calculateYearsBetween(birthDate, LocalDate.now());
        return (age >= 50) ? 2 : 0;
    }

    @Override
    public boolean isBirthdayLeaveEligible(Employee employee, LocalDate date) {
        LocalDate birthDate = getBirthDate(employee);
        if (birthDate == null) return false;
        return birthDate.getMonth() == date.getMonth() &&
                birthDate.getDayOfMonth() == date.getDayOfMonth();
    }

    @Override
    public int calculateMaternityLeaveDays(Employee employee, boolean multiplePregnancy) {
        String gender = getGender(employee);
        if (gender == null || !gender.equalsIgnoreCase("female")) return 0;
        return multiplePregnancy ? 126 + 14 : 112;
    }

    @Override
    public int calculatePaternityLeaveDays(Employee employee) {
        String gender = getGender(employee);
        if (gender == null || !gender.equalsIgnoreCase("male")) return 0;
        return 5;
    }

    @Override
    public int calculateBereavementLeaveDays(String relation) {
        if (relation == null) return 0;
        return switch (relation.toLowerCase()) {
            case "parent", "sibling", "child", "spouse" -> 3;
            case "grandparent", "aunt", "uncle", "in-law" -> 1;
            default -> 0;
        };
    }

    @Override
    public int calculateMarriageLeaveDays(Employee employee, boolean isFirstMarriage, boolean hasMarriageCertificate) {
        return (isFirstMarriage && hasMarriageCertificate) ? 3 : 0;
    }

    @Override
    public boolean isEligibleForMilitaryLeave(Employee employee) {
        return true; // Gerekirse askerlik belgesi ile kontrol yapılabilir
    }

    @Override
    public boolean canBorrowLeave(Employee employee, int requestedDays, int currentBorrowed) {
        LocalDate startDate = getEmploymentStartDate(employee);
        if (startDate == null) return false;

        int monthsWorked = Period.between(startDate, LocalDate.now()).getMonths() +
                Period.between(startDate, LocalDate.now()).getYears() * 12;

        boolean after3Months = monthsWorked >= 3;
        if (!after3Months) return false;

        return (requestedDays <= 5) && (currentBorrowed + requestedDays <= 10);
    }

    @Override
    public boolean isOfficialHoliday(LocalDate date) {
        return OFFICIAL_HOLIDAYS.contains(date) ||
                date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                date.getDayOfWeek() == DayOfWeek.SUNDAY; // isteğe bağlı: hafta sonunu da say
    }
}
