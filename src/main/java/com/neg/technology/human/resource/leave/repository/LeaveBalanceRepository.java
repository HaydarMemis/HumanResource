package com.neg.technology.human.resource.leave.repository;

import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    List<LeaveBalance> findByLeaveTypeId(Long leaveTypeId);

    boolean existsByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    void deleteById(Long id);
}
