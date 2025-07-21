package com.neg.hr.human.resouce.controller;

import com.neg.hr.human.resouce.entity.LeaveRequest;
import com.neg.hr.human.resouce.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<LeaveRequest> create(@RequestBody LeaveRequest leaveRequest) {
        LeaveRequest saved = leaveRequestService.save(leaveRequest);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveRequest> getById(@PathVariable Long id) {
        LeaveRequest leaveRequest = leaveRequestService.findById(id);
        return ResponseEntity.ok(leaveRequest);
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAll() {
        List<LeaveRequest> list = leaveRequestService.findAll();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leaveRequestService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
