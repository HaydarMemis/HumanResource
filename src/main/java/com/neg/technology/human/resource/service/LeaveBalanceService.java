package com.neg.technology.human.resource.service;

import com.neg.technology.human.resource.entity.LeaveBalance;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceService {
    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeIdAndDate(Integer year, Long employeeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    List<LeaveBalance> findByLeaveTypeIdAndDate(Long leaveTypeId, Integer year);

    boolean existsByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    LeaveBalance save(LeaveBalance leaveBalance);

    Optional<LeaveBalance> findById(Long id);

    List<LeaveBalance> findAll();

    void deleteById(Long id);

    LeaveBalance update(Long id, LeaveBalance leaveBalance);

    boolean existsById(Long id);
}
