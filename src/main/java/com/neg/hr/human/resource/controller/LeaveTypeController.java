package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.*;
import com.neg.hr.human.resource.dto.create.CreateLeaveTypeRequestDTO;
import com.neg.hr.human.resource.dto.entity.LeaveTypeEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveTypeRequestDTO;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveTypeMapper;
import com.neg.hr.human.resource.service.impl.LeaveTypeServiceImpl;
import com.neg.hr.human.resource.validator.LeaveTypeValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leave_types")
public class LeaveTypeController {

    private final LeaveTypeServiceImpl leaveTypeService;
    private final LeaveTypeValidator leaveTypeValidator;

    public LeaveTypeController(LeaveTypeServiceImpl leaveTypeService,
                               LeaveTypeValidator leaveTypeValidator) {
        this.leaveTypeService = leaveTypeService;
        this.leaveTypeValidator = leaveTypeValidator;
    }

    // POST - get all leave types
    @PostMapping("/getAll")
    public List<LeaveTypeEntityDTO> getAllLeaveTypes() {
        return leaveTypeService.findAll()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList();
    }

    // POST - get leave type by ID
    @PostMapping("/getById")
    public ResponseEntity<LeaveTypeEntityDTO> getLeaveTypeById(@Valid @RequestBody IdRequest request) {
        return leaveTypeService.findById(request.getId())
                .map(LeaveTypeMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - create leave type
    @PostMapping("/create")
    public ResponseEntity<LeaveTypeEntityDTO> createLeaveType(@Valid @RequestBody CreateLeaveTypeRequestDTO dto) {
        leaveTypeValidator.validateCreate(dto);
        LeaveType entity = LeaveTypeMapper.toEntity(dto);
        LeaveType saved = leaveTypeService.save(entity);
        return ResponseEntity.ok(LeaveTypeMapper.toDTO(saved));
    }

    // POST - update leave type
    @PostMapping("/update")
    public ResponseEntity<LeaveTypeEntityDTO> updateLeaveType(@Valid @RequestBody UpdateLeaveTypeRequestDTO dto) {
        if (!leaveTypeService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }

        leaveTypeValidator.validateUpdate(dto);

        LeaveType existing = leaveTypeService.findById(dto.getId());
        LeaveTypeMapper.updateEntity(existing, dto);
        LeaveType updated = leaveTypeService.save(existing);
        return ResponseEntity.ok(LeaveTypeMapper.toDTO(updated));
    }

    // POST - delete leave type
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveType(@Valid @RequestBody IdRequest request) {
        if (!leaveTypeService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        leaveTypeService.delete(request.getId());
        return ResponseEntity.noContent().build();
    }

    // POST - get leave type by name
    @PostMapping("/getByName")
    public ResponseEntity<LeaveTypeEntityDTO> getLeaveTypeByName(@Valid @RequestBody NameRequest request) {
        return leaveTypeService.findByName(request.getName())
                .map(LeaveTypeMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - get annual leave types
    @PostMapping("/getAnnual")
    public List<LeaveTypeEntityDTO> getAnnualLeaveTypes(@Valid @RequestBody BooleanRequest request) {
        return request.isValue()
                ? leaveTypeService.findByIsAnnualTrue()
                .stream().map(LeaveTypeMapper::toDTO).toList()
                : leaveTypeService.findByIsAnnualFalse()
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }

    // POST - get unpaid leave types
    @PostMapping("/getUnpaid")
    public List<LeaveTypeEntityDTO> getUnpaidLeaveTypes(@Valid @RequestBody BooleanRequest request) {
        return request.isValue()
                ? leaveTypeService.findByIsUnpaidTrue()
                .stream().map(LeaveTypeMapper::toDTO).toList()
                : leaveTypeService.findByIsUnpaidFalse()
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }

    // POST - get gender specific leave types
    @PostMapping("/getGenderSpecific")
    public List<LeaveTypeEntityDTO> getGenderSpecificLeaveTypes() {
        return leaveTypeService.findByGenderRequiredTrue()
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }

    // POST - get leave types by borrowable limit
    @PostMapping("/getByBorrowableLimit")
    public List<LeaveTypeEntityDTO> getLeaveTypesByBorrowableLimit(@Valid @RequestBody IntegerRequest request) {
        return leaveTypeService.findByBorrowableLimitGreaterThan(request.getValue())
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }

    // POST - get leave types by valid after days
    @PostMapping("/getByValidAfterDays")
    public List<LeaveTypeEntityDTO> getLeaveTypesByValidAfterDays(@Valid @RequestBody IntegerRequest request) {
        return leaveTypeService.findByValidAfterDaysGreaterThan(request.getValue())
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }
}