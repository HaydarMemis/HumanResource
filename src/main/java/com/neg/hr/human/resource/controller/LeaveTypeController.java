package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.business.LeaveTypeValidator;
import com.neg.hr.human.resource.dto.CreateLeaveTypeDTO;
import com.neg.hr.human.resource.dto.LeaveTypeDTO;
import com.neg.hr.human.resource.dto.UpdateLeaveTypeDTO;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveTypeMapper;
import com.neg.hr.human.resource.service.impl.LeaveTypeServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leave_types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeServiceImpl leaveTypeService;
    private final LeaveTypeValidator leaveTypeValidator;

    // GET all leave types (as DTO)
    @GetMapping
    public List<LeaveTypeDTO> getAllLeaveTypes() {
        return leaveTypeService.findAll()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // GET by ID (DTO)
    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeDTO> getLeaveTypeById(@PathVariable Long id) {
        return leaveTypeService.findById(id) != null
                ? ResponseEntity.ok(LeaveTypeMapper.toDTO(leaveTypeService.findById(id)))
                : ResponseEntity.notFound().build();
    }

    // POST - Create LeaveType
    @PostMapping
    public ResponseEntity<LeaveTypeDTO> createLeaveType(@Valid @RequestBody CreateLeaveTypeDTO dto) {
        leaveTypeValidator.validateCreate(dto);
        LeaveType entity = LeaveTypeMapper.toEntity(dto);
        LeaveType saved = leaveTypeService.save(entity);
        return ResponseEntity.ok(LeaveTypeMapper.toDTO(saved));
    }

    // PUT - Update LeaveType
    @PutMapping("/{id}")
    public ResponseEntity<LeaveTypeDTO> updateLeaveType(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateLeaveTypeDTO dto) {
        if (leaveTypeService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }

        leaveTypeValidator.validateUpdate(dto);

        LeaveType existing = leaveTypeService.findById(id);
        LeaveTypeMapper.updateEntity(existing, dto);
        LeaveType updated = leaveTypeService.save(existing);

        return ResponseEntity.ok(LeaveTypeMapper.toDTO(updated));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable Long id) {
        if (leaveTypeService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        leaveTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Additional filtered GET endpoints returning DTO lists
    @GetMapping("/name/{name}")
    public ResponseEntity<LeaveTypeDTO> getLeaveTypeByName(@PathVariable String name) {
        return leaveTypeService.findByName(name)
                .map(LeaveTypeMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/annual/true")
    public List<LeaveTypeDTO> getAnnualLeaveTypes() {
        return leaveTypeService.findByIsAnnualTrue()
                .stream().map(LeaveTypeMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/annual/false")
    public List<LeaveTypeDTO> getNonAnnualLeaveTypes() {
        return leaveTypeService.findByIsAnnualFalse()
                .stream().map(LeaveTypeMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/unpaid/true")
    public List<LeaveTypeDTO> getUnpaidLeaveTypes() {
        return leaveTypeService.findByIsUnpaidTrue()
                .stream().map(LeaveTypeMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/gender_required/specific")
    public List<LeaveTypeDTO> getGenderSpecificLeaveTypes() {
        return leaveTypeService.findByGenderRequiredTrue()
                .stream().map(LeaveTypeMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/borrowable_limit_greater_than/{limit}")
    public List<LeaveTypeDTO> getLeaveTypesByBorrowableLimit(@PathVariable Integer limit) {
        return leaveTypeService.findByBorrowableLimitGreaterThan(limit)
                .stream().map(LeaveTypeMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/valid_after_days_greater_than/{days}")
    public List<LeaveTypeDTO> getLeaveTypesByValidAfterDays(@PathVariable Integer days) {
        return leaveTypeService.findByValidAfterDaysGreaterThan(days)
                .stream().map(LeaveTypeMapper::toDTO).collect(Collectors.toList());
    }
}
