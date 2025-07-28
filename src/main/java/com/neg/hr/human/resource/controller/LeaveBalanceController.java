package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.LeaveBalance;
import com.neg.hr.human.resource.service.impl.LeaveBalanceServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave_balances")
public class LeaveBalanceController {

    private final LeaveBalanceServiceImpl leaveBalanceService;

    public LeaveBalanceController(LeaveBalanceServiceImpl leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    // Tüm LeaveBalance kayıtlarını getir
    @GetMapping
    public List<LeaveBalance> getAllLeaveBalances() {
        return leaveBalanceService.findAll();
    }

    // ID'ye göre LeaveBalance getir
    @GetMapping("/{id}")
    public ResponseEntity<LeaveBalance> getLeaveBalanceById(@PathVariable Long id) {
        Optional<LeaveBalance> leaveBalanceOpt = leaveBalanceService.findById(id);
        return leaveBalanceOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // LeaveBalance oluştur
    @PostMapping
    public LeaveBalance createLeaveBalance(@RequestBody LeaveBalance leaveBalance) {
        return leaveBalanceService.save(leaveBalance);
    }

    // LeaveBalance güncelle
    @PutMapping("/{id}")
    public ResponseEntity<LeaveBalance> updateLeaveBalance(@PathVariable Long id, @RequestBody LeaveBalance leaveBalance) {
        Optional<LeaveBalance> existingLeaveBalance = leaveBalanceService.findById(id);
        if (!existingLeaveBalance.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        leaveBalance.setId(id);
        LeaveBalance updated = leaveBalanceService.save(leaveBalance);
        return ResponseEntity.ok(updated);
    }

    // LeaveBalance sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveBalance(@PathVariable Long id) {
        Optional<LeaveBalance> existingLeaveBalance = leaveBalanceService.findById(id);
        if (!existingLeaveBalance.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        leaveBalanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Çalışan ID'sine göre LeaveBalance getir
    @GetMapping("/employee/{employeeId}")
    public List<LeaveBalance> getLeaveBalancesByEmployee(@PathVariable Long employeeId) {
        return leaveBalanceService.findByEmployeeId(employeeId);
    }

    // Çalışan ID ve yıla göre LeaveBalance getir
    @GetMapping("/employee/{employeeId}/year/{year}")
    public List<LeaveBalance> getLeaveBalancesByEmployeeAndYear(@PathVariable Long employeeId, @PathVariable Integer year) {
        return leaveBalanceService.findByEmployeeIdAndDate(year, employeeId);
    }

    // Çalışan ID ve leaveType ID'ye göre LeaveBalance getir
    @GetMapping("/employee/{employeeId}/leave_type/{leaveTypeId}")
    public ResponseEntity<LeaveBalance> getLeaveBalanceByEmployeeAndLeaveType(@PathVariable Long employeeId, @PathVariable Long leaveTypeId) {
        Optional<LeaveBalance> leaveBalanceOpt = leaveBalanceService.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId);
        return leaveBalanceOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Çalışan ID, leaveType ID ve yıla göre LeaveBalance getir
    @GetMapping("/employee/{employeeId}/leave_type/{leaveTypeId}/year/{year}")
    public ResponseEntity<LeaveBalance> getLeaveBalanceByEmployeeLeaveTypeAndYear(@PathVariable Long employeeId, @PathVariable Long leaveTypeId, @PathVariable Integer year) {
        Optional<LeaveBalance> leaveBalanceOpt = leaveBalanceService.findByEmployeeIdAndLeaveTypeIdAndDate(employeeId, leaveTypeId, year);
        return leaveBalanceOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // LeaveType ID ve yıla göre LeaveBalance getir
    @GetMapping("/leave_type/{leaveTypeId}/year/{year}")
    public List<LeaveBalance> getLeaveBalancesByLeaveTypeAndYear(@PathVariable Long leaveTypeId, @PathVariable Integer year) {
        return leaveBalanceService.findByLeaveTypeIdAndDate(leaveTypeId, year);
    }
}
