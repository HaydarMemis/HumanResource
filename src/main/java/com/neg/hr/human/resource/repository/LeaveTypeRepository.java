package com.neg.hr.human.resource.repository;

import com.neg.hr.human.resource.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByName(String name);

    List<LeaveType> findByIsAnnualTrue();

    List<LeaveType> findByIsAnnualFalse();

    List<LeaveType> findByIsUnpaidTrue();

    // Updated to reflect genderRequired is now a String
    List<LeaveType> findByGenderRequiredIsNotNull();

    List<LeaveType> findByBorrowableLimitGreaterThan(Integer limit);

    List<LeaveType> findByValidAfterDaysGreaterThan(Integer days);
}
