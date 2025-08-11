package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.BooleanRequest;
import com.neg.hr.human.resource.dto.IdRequest;
import com.neg.hr.human.resource.dto.IntegerRequest;
import com.neg.hr.human.resource.dto.NameRequest;
import com.neg.hr.human.resource.dto.create.CreateLeaveTypeRequestDTO;
import com.neg.hr.human.resource.dto.entity.LeaveTypeEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveTypeRequestDTO;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveTypeMapper;
import com.neg.hr.human.resource.service.LeaveTypeService;
import com.neg.hr.human.resource.validator.LeaveTypeValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave_types")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;
    private final LeaveTypeValidator leaveTypeValidator;

    public LeaveTypeController(LeaveTypeService leaveTypeService,
                               LeaveTypeValidator leaveTypeValidator) {
        this.leaveTypeService = leaveTypeService;
        this.leaveTypeValidator = leaveTypeValidator;
    }

    @PostMapping("/getAll")
    public List<LeaveTypeEntityDTO> getAllLeaveTypes() {
        return leaveTypeService.findAll()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList();
    }

    @PostMapping("/getById")
    public ResponseEntity<LeaveTypeEntityDTO> getLeaveTypeById(@Valid @RequestBody IdRequest request) {
        Optional<LeaveType> leaveTypeOpt = leaveTypeService.findById(request.getId());
        if (leaveTypeOpt.isPresent()) {
            LeaveTypeEntityDTO dto = LeaveTypeMapper.toDTO(leaveTypeOpt.get());
            return ResponseEntity.ok(dto);  // Explicitly using ok(T)
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<LeaveTypeEntityDTO> createLeaveType(@Valid @RequestBody CreateLeaveTypeRequestDTO dto) {
        leaveTypeValidator.validateCreate(dto);
        LeaveType entity = LeaveTypeMapper.toEntity(dto);
        LeaveType saved = leaveTypeService.save(entity);
        return ResponseEntity.ok(LeaveTypeMapper.toDTO(saved));
    }

    @PostMapping("/update")
    public ResponseEntity<LeaveTypeEntityDTO> updateLeaveType(@Valid @RequestBody UpdateLeaveTypeRequestDTO dto) {

        if (!leaveTypeService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }

        leaveTypeValidator.validateUpdate(dto);

        Optional<LeaveType> existingOpt = leaveTypeService.findById(dto.getId());
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LeaveType existing = existingOpt.get();
        LeaveTypeMapper.updateEntity(existing, dto);
        LeaveType updated = leaveTypeService.save(existing);

        return ResponseEntity.ok(LeaveTypeMapper.toDTO(updated));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveType(@Valid @RequestBody IdRequest request) {
        if (!leaveTypeService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        leaveTypeService.delete(request.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/getByName")
    public ResponseEntity<LeaveTypeEntityDTO> getLeaveTypeByName(@Valid @RequestBody NameRequest request) {
        return leaveTypeService.findByName(request.getName())
                .map(LeaveTypeMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/getAnnual")
    public List<LeaveTypeEntityDTO> getAnnualLeaveTypes(@Valid @RequestBody BooleanRequest request) {
        return request.isValue()
                ? leaveTypeService.findByIsAnnualTrue()
                .stream().map(LeaveTypeMapper::toDTO).toList()
                : leaveTypeService.findByIsAnnualFalse()
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }

    @PostMapping("/getUnpaid")
    public List<LeaveTypeEntityDTO> getUnpaidLeaveTypes(@Valid @RequestBody BooleanRequest request) {
        return request.isValue()
                ? leaveTypeService.findByIsUnpaidTrue()
                .stream().map(LeaveTypeMapper::toDTO).toList()
                : leaveTypeService.findByIsUnpaidFalse()
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }

    @PostMapping("/getGenderSpecific")
    public List<LeaveTypeEntityDTO> getGenderSpecificLeaveTypes() {
        return leaveTypeService.findByGenderRequiredTrue()
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }

    @PostMapping("/getByBorrowableLimit")
    public List<LeaveTypeEntityDTO> getLeaveTypesByBorrowableLimit(@Valid @RequestBody IntegerRequest request) {
        return leaveTypeService.findByBorrowableLimitGreaterThan(request.getValue())
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }

    @PostMapping("/getByValidAfterDays")
    public List<LeaveTypeEntityDTO> getLeaveTypesByValidAfterDays(@Valid @RequestBody IntegerRequest request) {
        return leaveTypeService.findByValidAfterDaysGreaterThan(request.getValue())
                .stream().map(LeaveTypeMapper::toDTO).toList();
    }
}