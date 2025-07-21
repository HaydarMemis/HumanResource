package com.neg.hr.human.resouce.repository;

import com.neg.hr.human.resouce.entity.LeaveRequest;
import com.neg.hr.human.resouce.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    List<LeaveRequest> findByStartDateBetween(LocalDate start, LocalDate end);

    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, String status);

    List<LeaveRequest> findByStatus(String status);  // örn: APPROVED, PENDING, REJECTED, CANCELLED

    List<LeaveRequest> findByIsCancelledTrue();

    List<LeaveRequest> findByApprovedById(Long approverId);

    List<LeaveRequest> findByLeaveType(LeaveType leaveType);

    List<LeaveRequest> findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(
            Long employeeId,
            Long leaveTypeId,
            LocalDate startDate,
            LocalDate endDate
    );

    //  Belirli personelin aynı tarihlerde talep ettiği izin kontrolü (çakışma)
    @Query("""
        SELECT lr FROM LeaveRequest lr 
        WHERE lr.employee.id = :employeeId 
        AND lr.startDate <= :endDate 
        AND lr.endDate >= :startDate
        AND lr.isCancelled = false
    """)
    List<LeaveRequest> findOverlappingRequests(Long employeeId, LocalDate startDate, LocalDate endDate);
}
