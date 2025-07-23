package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.LeaveRequest;
import com.neg.hr.human.resource.service.LeaveRequestService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @GetMapping
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getLeaveRequestById(@PathVariable Long id) {
        LeaveRequest leaveRequest = leaveRequestService.findById(id);
        return ResponseEntity.ok(leaveRequest);
    }

    @PostMapping
    public LeaveRequest createLeaveRequest(@RequestBody LeaveRequest leaveRequest) {
        return leaveRequestService.save(leaveRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveRequest> updateLeaveRequest(@PathVariable Long id, @RequestBody LeaveRequest leaveRequest) {
        LeaveRequest updated = leaveRequestService.update(id, leaveRequest);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long id) {
        leaveRequestService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequest> getLeaveRequestsByEmployee(@PathVariable Long employeeId) {
        return leaveRequestService.findByEmployeeId(employeeId);
    }

    @GetMapping("/status/{status}")
    public List<LeaveRequest> getLeaveRequestsByStatus(@PathVariable String status) {
        return leaveRequestService.findByStatus(status);
    }

    @GetMapping("/cancelled")
    public List<LeaveRequest> getCancelledLeaveRequests() {
        return leaveRequestService.findByIsCancelledTrue();
    }

    @GetMapping("/approver/{approverId}")
    public List<LeaveRequest> getLeaveRequestsByApprover(@PathVariable Long approverId) {
        return leaveRequestService.findByApprovedById(approverId);
    }

    @GetMapping("/employee/{employeeId}/status/{status}")
    public List<LeaveRequest> getLeaveRequestsByEmployeeAndStatus(@PathVariable Long employeeId, @PathVariable String status) {
        return leaveRequestService.findByEmployeeIdAndStatus(employeeId, status);
    }

    @GetMapping("/date-range")
    public List<LeaveRequest> getLeaveRequestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findByStartDateBetween(start, end);
    }

    @GetMapping("/employee/{employeeId}/leave-type/{leaveTypeId}/date-range")
    public List<LeaveRequest> getLeaveRequestsByEmployeeLeaveTypeAndDateRange(
            @PathVariable Long employeeId,
            @PathVariable Long leaveTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(employeeId, leaveTypeId, start, end);
    }

    @GetMapping("/overlapping")
    public List<LeaveRequest> getOverlappingLeaveRequests(
            @RequestParam Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return leaveRequestService.findOverlappingRequests(employeeId, start, end);
    }
}
