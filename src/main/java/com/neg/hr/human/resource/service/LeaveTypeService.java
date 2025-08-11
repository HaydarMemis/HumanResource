package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.LeaveType;

import java.util.List;
import java.util.Optional;

public interface LeaveTypeService {
    Optional<LeaveType> findByName(String name);

    List<LeaveType> findByIsAnnualTrue();

    List<LeaveType> findByIsAnnualFalse();

    List<LeaveType> findByIsUnpaidTrue();

    List<LeaveType> findByGenderRequiredTrue();

    List<LeaveType> findByBorrowableLimitGreaterThan(Integer limit);

    List<LeaveType> findByValidAfterDaysGreaterThan(Integer days);

    LeaveType save(LeaveType leaveType);

    Optional<LeaveType> findById(Long id);

    List<LeaveType> findAll();

    void delete(Long id);

    LeaveType update(Long id, LeaveType leaveType);

    boolean existsById(Long id);

    List<LeaveType> findByIsUnpaidFalse();
}
