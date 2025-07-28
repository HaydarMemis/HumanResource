package com.neg.hr.human.resource.service.impl;

import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.service.LeavePolicyService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
public class LeavePolicyServiceImpl implements LeavePolicyService {

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
        LocalDate startDate = getEmploymentStartDate(employee);

        int age = calculateYearsBetween(birthDate, LocalDate.now());
        int seniority = calculateYearsBetween(startDate, LocalDate.now());

        return (age >= 50 && seniority >= 1) ? 20 : 0;
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
        return multiplePregnancy ? 224 : 168;
    }

    @Override
    public int calculateBereavementLeaveDays(String relation) {
        if (relation == null) return 0;
        return switch (relation.toLowerCase()) {
            case "parent", "sibling", "child", "spouse" -> 3;
            case "grandparent", "aunt", "uncle" -> 1;
            default -> 0;
        };
    }

    @Override
    public int calculateMarriageLeaveDays(boolean isFirstMarriage, boolean hasMarriageCertificate) {
        return (isFirstMarriage && hasMarriageCertificate) ? 3 : 0;
    }

    @Override
    public int calculateMilitaryLeaveDays() {
        return 30;
    }

    @Override
    public boolean canBorrowLeave(Employee employee, int requestedDays, int currentBorrowed) {
        LocalDate startDate = getEmploymentStartDate(employee);
        if (startDate == null) return false;

        int yearsWorked = calculateYearsBetween(startDate, LocalDate.now());
        int maxBorrowable = (yearsWorked < 1) ? 3 : 7;

        return (currentBorrowed + requestedDays) <= maxBorrowable;
    }
}
