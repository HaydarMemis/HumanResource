package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.LeaveRequest;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.exception.ResourceNotFoundException;
import com.neg.hr.human.resource.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService implements LeaveRequestInterface{

    private final LeaveRequestRepository leaveRequestRepository;

    @Override
    public List<LeaveRequest> findByEmployeeId(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<LeaveRequest> findByStartDateBetween(LocalDate start, LocalDate end) {
        return leaveRequestRepository.findByStartDateBetween(start, end);
    }

    @Override
    public List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, String status) {
        return leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, status);
    }

    @Override
    public List<LeaveRequest> findByStatus(String status) {
        return leaveRequestRepository.findByStatus(status);
    }

    @Override
    public List<LeaveRequest> findByIsCancelledTrue() {
        return leaveRequestRepository.findByIsCancelledTrue();
    }

    @Override
    public List<LeaveRequest> findByApprovedById(Long approverId) {
        return leaveRequestRepository.findByApprovedById(approverId);
    }

    @Override
    public List<LeaveRequest> findByLeaveType(LeaveType leaveType) {
        return leaveRequestRepository.findByLeaveType(leaveType);
    }

    @Override
    public List<LeaveRequest> findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate) {
        return leaveRequestRepository.findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(employeeId, leaveTypeId, startDate, endDate);
    }

    @Override
    public List<LeaveRequest> findOverlappingRequests(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return leaveRequestRepository.findOverlappingRequests(employeeId, startDate, endDate);
    }

    public LeaveRequest save(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request",id));
    }

    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAll();
    }

    public void deleteById(Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave Request", id);
        }
        leaveRequestRepository.deleteById(id);
    }

    public LeaveRequest update(Long id, LeaveRequest leaveRequest) {
        LeaveRequest existing = leaveRequestRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Leave Request",id));

        existing.setEmployee(leaveRequest.getEmployee());
        existing.setRequestedDays(leaveRequest.getRequestedDays());
        existing.setStatus(leaveRequest.getStatus());
        existing.setReason(leaveRequest.getReason());
        existing.setApprovedBy(leaveRequest.getApprovedBy());
        existing.setApprovedAt(leaveRequest.getApprovedAt());
        existing.setApprovalNote(leaveRequest.getApprovalNote());
        existing.setIsCancelled(leaveRequest.getIsCancelled());
        existing.setCancelledAt(leaveRequest.getCancelledAt());
        existing.setCancellationReason(leaveRequest.getCancellationReason());
        existing.setStartDate(leaveRequest.getStartDate());
        existing.setEndDate(leaveRequest.getEndDate());
        existing.setIsCancelled(leaveRequest.getIsCancelled());
        existing.setApprovedBy(leaveRequest.getApprovedBy());
        existing.setLeaveType(leaveRequest.getLeaveType());

        return leaveRequestRepository.save(existing);
    }
}
