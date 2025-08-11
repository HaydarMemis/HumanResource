package com.neg.technology.human.resource.LeaveBalance.repository;

import com.neg.technology.human.resource.LeaveBalance.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeIdAndDate(Integer year, Long employeeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    List<LeaveBalance> findByLeaveTypeIdAndDate(Long leaveTypeId, Integer year);

    boolean existsByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    void deleteById(Long id);
}
