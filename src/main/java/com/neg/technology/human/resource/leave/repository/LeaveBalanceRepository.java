package com.neg.technology.human.resource.leave.repository;

import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeIdAndDate(Long employeeId, Integer year);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    List<LeaveBalance> findByLeaveTypeIdAndDate(Long leaveTypeId, Integer year);

    void deleteById(Long id);

}
