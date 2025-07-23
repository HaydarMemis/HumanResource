package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.LeaveBalance;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceInterface {
    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeIdAndDate(Integer year, Long employeeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    List<LeaveBalance> findByLeaveTypeIdAndDate(Long leaveTypeId, Integer year);

    boolean existsByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    public LeaveBalance save(LeaveBalance leaveBalance);

    public Optional<LeaveBalance> findById(Long id);

    public List<LeaveBalance> findAll();

    void deleteById(Long id);

    public LeaveBalance update(Long id, LeaveBalance leaveBalance);
}
