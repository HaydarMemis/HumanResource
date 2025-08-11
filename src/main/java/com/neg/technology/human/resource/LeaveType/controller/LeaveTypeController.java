package com.neg.technology.human.resource.LeaveType.controller;

import com.neg.technology.human.resource.dto.create.CreateLeaveTypeRequestDTO;
import com.neg.technology.human.resource.dto.entity.LeaveTypeEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateLeaveTypeRequestDTO;
import com.neg.technology.human.resource.dto.utilities.BooleanRequest;
import com.neg.technology.human.resource.dto.utilities.IdRequest;
import com.neg.technology.human.resource.dto.utilities.IntegerRequest;
import com.neg.technology.human.resource.dto.utilities.NameRequest;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import com.neg.technology.human.resource.LeaveType.model.mapper.LeaveTypeMapper;
import com.neg.technology.human.resource.LeaveType.service.LeaveTypeService;
import com.neg.technology.human.resource.LeaveType.validator.LeaveTypeValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "LeaveType Controller", description = "Operations related to leave type management")
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

    @Operation(summary = "Get all leave types", description = "Retrieve a list of all leave types")
    @ApiResponse(responseCode = "200", description = "List of leave types retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<LeaveTypeEntityDTO>> getAllLeaveTypes() {
        List<LeaveTypeEntityDTO> leaveTypes = leaveTypeService.findAll()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(leaveTypes);
    }

    @Operation(summary = "Get leave type by ID", description = "Retrieve a leave type by its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type found"),
            @ApiResponse(responseCode = "404", description = "Leave type not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<LeaveTypeEntityDTO> getLeaveTypeById(
            @Parameter(description = "ID of the leave type to be retrieved", required = true)
            @Valid @RequestBody IdRequest request) {
        Optional<LeaveType> leaveTypeOpt = leaveTypeService.findById(request.getId());
        return leaveTypeOpt
                .map(leaveType -> ResponseEntity.ok(LeaveTypeMapper.toDTO(leaveType)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new leave type", description = "Create a new leave type record")
    @ApiResponse(responseCode = "200", description = "Leave type created successfully")
    @PostMapping("/create")
    public ResponseEntity<LeaveTypeEntityDTO> createLeaveType(
            @Parameter(description = "Leave type data for creation", required = true)
            @Valid @RequestBody CreateLeaveTypeRequestDTO dto) {
        leaveTypeValidator.validateCreate(dto);
        LeaveType entity = LeaveTypeMapper.toEntity(dto);
        LeaveType saved = leaveTypeService.save(entity);
        return ResponseEntity.ok(LeaveTypeMapper.toDTO(saved));
    }

    @Operation(summary = "Update existing leave type", description = "Update details of an existing leave type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type updated successfully"),
            @ApiResponse(responseCode = "404", description = "Leave type not found")
    })
    @PostMapping("/update")
    public ResponseEntity<LeaveTypeEntityDTO> updateLeaveType(
            @Parameter(description = "Leave type data for update", required = true)
            @Valid @RequestBody UpdateLeaveTypeRequestDTO dto) {

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

    @Operation(summary = "Delete leave type", description = "Delete a leave type by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Leave type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Leave type not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveType(
            @Parameter(description = "ID of the leave type to be deleted", required = true)
            @Valid @RequestBody IdRequest request) {
        if (!leaveTypeService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        leaveTypeService.delete(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get leave type by name", description = "Retrieve a leave type by its name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type found"),
            @ApiResponse(responseCode = "404", description = "Leave type not found")
    })
    @PostMapping("/getByName")
    public ResponseEntity<LeaveTypeEntityDTO> getLeaveTypeByName(
            @Parameter(description = "Name of the leave type to be retrieved", required = true)
            @Valid @RequestBody NameRequest request) {
        return leaveTypeService.findByName(request.getName())
                .map(LeaveTypeMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get annual leave types", description = "Retrieve leave types filtered by whether they are annual")
    @ApiResponse(responseCode = "200", description = "List of leave types filtered by annual status retrieved successfully")
    @PostMapping("/getAnnual")
    public ResponseEntity<List<LeaveTypeEntityDTO>> getAnnualLeaveTypes(
            @Parameter(description = "Boolean value to filter annual leave types", required = true)
            @Valid @RequestBody BooleanRequest request) {
        List<LeaveTypeEntityDTO> leaveTypes = request.isValue()
                ? leaveTypeService.findByIsAnnualTrue()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList()
                : leaveTypeService.findByIsAnnualFalse()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(leaveTypes);
    }

    @Operation(summary = "Get unpaid leave types", description = "Retrieve leave types filtered by whether they are unpaid")
    @ApiResponse(responseCode = "200", description = "List of leave types filtered by unpaid status retrieved successfully")
    @PostMapping("/getUnpaid")
    public ResponseEntity<List<LeaveTypeEntityDTO>> getUnpaidLeaveTypes(
            @Parameter(description = "Boolean value to filter unpaid leave types", required = true)
            @Valid @RequestBody BooleanRequest request) {
        List<LeaveTypeEntityDTO> leaveTypes = request.isValue()
                ? leaveTypeService.findByIsUnpaidTrue()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList()
                : leaveTypeService.findByIsUnpaidFalse()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(leaveTypes);
    }

    @Operation(summary = "Get gender specific leave types", description = "Retrieve leave types that require gender specification")
    @ApiResponse(responseCode = "200", description = "List of gender specific leave types retrieved successfully")
    @PostMapping("/getGenderSpecific")
    public ResponseEntity<List<LeaveTypeEntityDTO>> getGenderSpecificLeaveTypes() {
        List<LeaveTypeEntityDTO> leaveTypes = leaveTypeService.findByGenderRequiredTrue()
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(leaveTypes);
    }

    @Operation(summary = "Get leave types by borrowable limit", description = "Retrieve leave types with borrowable limit greater than the specified value")
    @ApiResponse(responseCode = "200", description = "List of leave types filtered by borrowable limit retrieved successfully")
    @PostMapping("/getByBorrowableLimit")
    public ResponseEntity<List<LeaveTypeEntityDTO>> getLeaveTypesByBorrowableLimit(
            @Parameter(description = "Minimum borrowable limit value", required = true)
            @Valid @RequestBody IntegerRequest request) {
        List<LeaveTypeEntityDTO> leaveTypes = leaveTypeService.findByBorrowableLimitGreaterThan(request.getValue())
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(leaveTypes);
    }

    @Operation(summary = "Get leave types by valid after days", description = "Retrieve leave types with valid after days greater than the specified value")
    @ApiResponse(responseCode = "200", description = "List of leave types filtered by valid after days retrieved successfully")
    @PostMapping("/getByValidAfterDays")
    public ResponseEntity<List<LeaveTypeEntityDTO>> getLeaveTypesByValidAfterDays(
            @Parameter(description = "Minimum valid after days value", required = true)
            @Valid @RequestBody IntegerRequest request) {
        List<LeaveTypeEntityDTO> leaveTypes = leaveTypeService.findByValidAfterDaysGreaterThan(request.getValue())
                .stream()
                .map(LeaveTypeMapper::toDTO)
                .toList();
        return ResponseEntity.ok(leaveTypes);
    }
}
