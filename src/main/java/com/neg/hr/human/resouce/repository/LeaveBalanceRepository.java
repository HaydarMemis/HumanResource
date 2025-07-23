package com.neg.hr.human.resouce.repository;

import com.neg.hr.human.resouce.entity.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    List<LeaveBalance> findByEmployeeId(Long employeeId);

    List<LeaveBalance> findByEmployeeIdAndDate(Integer year, Long employeeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId);

    Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    List<LeaveBalance> findByLeaveTypeIdAndDate(Long leaveTypeId, Integer year);

    //  Kullanıcının izin hakkı var mı? (exists kontrolü)
    boolean existsByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year);

    void deleteById(Long id);
}
