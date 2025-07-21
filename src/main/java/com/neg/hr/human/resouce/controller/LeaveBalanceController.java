package com.neg.hr.human.resouce.controller;

import com.neg.hr.human.resouce.entity.Employee;
import com.neg.hr.human.resouce.entity.LeaveBalance;
import com.neg.hr.human.resouce.service.LeaveBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave_balances")
@RequiredArgsConstructor
public class LeaveBalanceController {
    private final LeaveBalanceService leaveBalanceService;

    // CREATE
    @PostMapping
    public ResponseEntity<LeaveBalance> create(@RequestBody LeaveBalance leaveBalance) {
        LeaveBalance saved = leaveBalanceService.save(leaveBalance);
        return ResponseEntity.ok(saved);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<LeaveBalance>> getAll() {
        List<LeaveBalance> leaveBalances = leaveBalanceService.findAll();
        return ResponseEntity.ok(leaveBalances);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveBalance> getById(@PathVariable Long id) {
        LeaveBalance leaveBalance = leaveBalanceService.findById(id);
        return ResponseEntity.ok(leaveBalance);
    }


    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<LeaveBalance> update(@PathVariable Long id, @RequestBody LeaveBalance leaveBalance) {
        LeaveBalance updated = leaveBalanceService.update(id, leaveBalance);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveBalanceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
