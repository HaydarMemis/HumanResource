package com.neg.technology.human.resource.leave.repository;

import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYear(Long employeeId, Long leaveTypeId, Integer year);

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeIdAndYear(Long employeeId, Integer year);

    List<LeaveBalance> findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(Long employeeId, Long leaveTypeId);

    List<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndYearBetween(Long employeeId, Long leaveTypeId,Integer startYear, Integer endYear);

    List<LeaveBalance> findByLeaveTypeIdAndYearBetween(Long leaveTypeId, Integer startYear, Integer endYear);

    List<LeaveBalance> findByLeaveTypeIdAndYear(Long leaveTypeId, Integer year);

    List<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

}
