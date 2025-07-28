package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.service.impl.LeaveTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave_types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeServiceImpl leaveTypeService;

    // Get all leave types
    @GetMapping
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeService.findAll();
    }

    // Get leave type by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveType> getLeaveTypeById(@PathVariable Long id) {
        try {
            LeaveType leaveType = leaveTypeService.findById(id);
            return ResponseEntity.ok(leaveType);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new leave type
    @PostMapping
    public LeaveType createLeaveType(@RequestBody LeaveType leaveType) {
        return leaveTypeService.save(leaveType);
    }

    // Update leave type
    @PutMapping("/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(@PathVariable Long id, @RequestBody LeaveType leaveType) {
        try {
            LeaveType updated = leaveTypeService.update(id, leaveType);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete leave type
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        try {
            leaveTypeService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get leave types by name
    @GetMapping("/name/{name}")
    public ResponseEntity<LeaveType> getLeaveTypeByName(@PathVariable String name) {
        return leaveTypeService.findByName(name)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get annual leave types
    @GetMapping("/annual/true")
    public List<LeaveType> getAnnualLeaveTypes() {
        return leaveTypeService.findByIsAnnualTrue();
    }

    // Get non-annual leave types
    @GetMapping("/annual/false")
    public List<LeaveType> getNonAnnualLeaveTypes() {
        return leaveTypeService.findByIsAnnualFalse();
    }

    // Get unpaid leave types
    @GetMapping("/unpaid/true")
    public List<LeaveType> getUnpaidLeaveTypes() {
        return leaveTypeService.findByIsUnpaidTrue();
    }

    // Get leave types requiring gender
    @GetMapping("/gender_required/true")
    public List<LeaveType> getGenderRequiredLeaveTypes() {
        return leaveTypeService.findByGenderRequiredTrue();
    }

    // Get leave types with borrowable limit greater than specified limit
    @GetMapping("/borrowable_limit_greater_than/{limit}")
    public List<LeaveType> getLeaveTypesByBorrowableLimit(@PathVariable Integer limit) {
        return leaveTypeService.findByBorrowableLimitGreaterThan(limit);
    }

    // Get leave types with valid after days greater than specified days
    @GetMapping("/valid_after_days_greater_than/{days}")
    public List<LeaveType> getLeaveTypesByValidAfterDays(@PathVariable Integer days) {
        return leaveTypeService.findByValidAfterDaysGreaterThan(days);
    }
}
