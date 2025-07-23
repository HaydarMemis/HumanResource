package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.LeaveRequest;
import com.neg.hr.human.resource.entity.LeaveType;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestInterface {
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

    List<LeaveRequest> findOverlappingRequests(Long employeeId, LocalDate startDate, LocalDate endDate);

    public LeaveRequest save(LeaveRequest leaveRequest);

    public LeaveRequest findById(Long id);

    public List<LeaveRequest> findAll();

    public void deleteById(Long id);
}
