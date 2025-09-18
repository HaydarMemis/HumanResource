package com.neg.technology.human.resource.leave.repository;

import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    // Çalışan + LeaveType ile tek bir kayıt bulma
    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    // Çalışan Id ile tüm kayıtları listeleme
    List<LeaveBalance> findByEmployeeId(Long employeeId);

    // LeaveType Id ile tüm kayıtları listeleme
    List<LeaveBalance> findByLeaveTypeId(Long leaveTypeId);
}
