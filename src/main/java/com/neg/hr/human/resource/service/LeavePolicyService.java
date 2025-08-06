package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Employee;

import java.time.LocalDate;

public interface LeavePolicyService {

    int calculateAnnualLeaveDays(Employee employee);

    int calculateAgeBasedLeaveBonus(Employee employee);

    boolean isBirthdayLeaveEligible(Employee employee, LocalDate date);

    int calculateMaternityLeaveDays(Employee employee, boolean multiplePregnancy);

    int calculatePaternityLeaveDays(Employee employee);

    int calculateBereavementLeaveDays(String relation);

    int calculateMarriageLeaveDays(Employee employee, boolean isFirstMarriage, boolean hasMarriageCertificate);

    boolean isEligibleForMilitaryLeave(Employee employee);

    boolean canBorrowLeave(Employee employee, int requestedDays, int currentBorrowed);

    boolean isOfficialHoliday(LocalDate date);
}
