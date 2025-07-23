package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Employee;
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
        if (employee.getPerson() == null) return null;
        return employee.getPerson().getBirthDate();
    }

    private String getGender(Employee employee) {
        if (employee.getPerson() == null) return null;
        return employee.getPerson().getGender();
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
        else if (yearsWorked < 15) return 20;
        else return 26;
    }

    @Override
    public int calculateAgeBasedLeaveBonus(Employee employee) {
        LocalDate birthDate = getBirthDate(employee);
        int age = calculateYearsBetween(birthDate, LocalDate.now());
        int seniority = calculateYearsBetween(getEmploymentStartDate(employee), LocalDate.now());

        return (age >= 50 && seniority >= 1) ? 20 : 0;
    }

    @Override
    public boolean isBirthdayLeaveEligible(Employee employee, LocalDate date) {
        LocalDate birthDate = getBirthDate(employee);
        if (birthDate == null) return false;

        return birthDate.getMonth() == date.getMonth()
                && birthDate.getDayOfMonth() == date.getDayOfMonth();
    }

    @Override
    public int calculateMaternityLeaveDays(Employee employee, boolean multiplePregnancy) {
        String gender = getGender(employee);
        if (gender == null || !gender.equalsIgnoreCase("female")) {
            return 0;
        }
        return multiplePregnancy ? 224 : 168; // gün olarak (32 veya 24 hafta)
    }

    @Override
    public int calculateBereavementLeaveDays(String relation) {
        switch (relation.toLowerCase()) {
            case "parent":
            case "sibling":
            case "child":
            case "spouse":
                return 3;
            case "grandparent":
            case "aunt":
            case "uncle":
                return 1;
            default:
                return 0;
        }
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

        int maxBorrowable = 7;
        int yearsWorked = calculateYearsBetween(startDate, LocalDate.now());

        if (yearsWorked < 1) maxBorrowable = 3;

        return (currentBorrowed + requestedDays) <= maxBorrowable;
    }
}
