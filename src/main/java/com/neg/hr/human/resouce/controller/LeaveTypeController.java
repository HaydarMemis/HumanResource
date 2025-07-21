package com.neg.hr.human.resouce.controller;

import com.neg.hr.human.resouce.entity.LeaveType;
import com.neg.hr.human.resouce.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave_types")
@RequiredArgsConstructor
public class LeaveTypeController {
    private final LeaveTypeService leaveTypeService;

    //all
    @GetMapping
    public ResponseEntity<List<LeaveType>> getAll() {
        List<LeaveType> leaveTypes = leaveTypeService.findAll();
        return ResponseEntity.ok(leaveTypes);
    }

    //id find
    @GetMapping("/{id}")
    public ResponseEntity<LeaveType> getById(@PathVariable Long id) {
        LeaveType leaveType = leaveTypeService.findById(id);
        return ResponseEntity.ok(leaveType);
    }

    @PostMapping
    public ResponseEntity<LeaveType> create(@RequestBody LeaveType leaveType) {
        LeaveType saved = leaveTypeService.save(leaveType);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeaveType> update(@PathVariable Long id,@RequestBody LeaveType leaveType) {
        LeaveType updated = leaveTypeService.update(id,leaveType);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("{/id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
