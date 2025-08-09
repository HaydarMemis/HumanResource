package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.LeaveRequest;
import com.neg.hr.human.resource.entity.LeaveType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveRequestService {

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    List<LeaveRequest> findByStartDateBetween(LocalDate start, LocalDate end);

    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, String status);

    List<LeaveRequest> findByStatus(String status);

    List<LeaveRequest> findByIsCancelledTrue();

    List<LeaveRequest> findByApprovedById(Long approverId);

    List<LeaveRequest> findByLeaveType(LeaveType leaveType);

    List<LeaveRequest> findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate);

    List<LeaveRequest> findOverlappingRequests(Long employeeId, LocalDate startDate, LocalDate endDate);

    LeaveRequest save(LeaveRequest leaveRequest);

    Optional<LeaveRequest> findById(Long id);

    List<LeaveRequest> findAll();

    void deleteById(Long id);

    LeaveRequest update(Long id, LeaveRequest leaveRequest);

    boolean existsById(Long id);
}
