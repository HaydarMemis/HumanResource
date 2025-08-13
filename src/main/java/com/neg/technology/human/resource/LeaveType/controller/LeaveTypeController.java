package com.neg.technology.human.resource.LeaveType.controller;

import com.neg.technology.human.resource.LeaveType.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.response.LeaveTypeResponse;
import com.neg.technology.human.resource.LeaveType.model.response.LeaveTypeResponseList;
import com.neg.technology.human.resource.LeaveType.service.LeaveTypeService;
import com.neg.technology.human.resource.Utility.request.BooleanRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.IntegerRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "LeaveType Controller", description = "Operations related to leave type management")
@RestController
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    public LeaveTypeController(LeaveTypeService leaveTypeService) {
        this.leaveTypeService = leaveTypeService;
    }

    @Operation(summary = "Get all leave types")
    @PostMapping("/getAll")
    public ResponseEntity<LeaveTypeResponseList> getAllLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getAll());
    }

    @Operation(summary = "Get leave type by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave type found"),
            @ApiResponse(responseCode = "404", description = "Leave type not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<LeaveTypeResponse> getLeaveTypeById(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(leaveTypeService.getById(request));
    }

    @Operation(summary = "Create new leave type")
    @PostMapping("/create")
    public ResponseEntity<LeaveTypeResponse> createLeaveType(@Valid @RequestBody CreateLeaveTypeRequest dto) {
        return ResponseEntity.ok(leaveTypeService.create(dto));
    }

    @Operation(summary = "Update existing leave type")
    @PostMapping("/update")
    public ResponseEntity<LeaveTypeResponse> updateLeaveType(@Valid @RequestBody UpdateLeaveTypeRequest dto) {
        return ResponseEntity.ok(leaveTypeService.update(dto));
    }

    @Operation(summary = "Delete leave type")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveType(@Valid @RequestBody IdRequest request) {
        leaveTypeService.delete(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get leave type by name")
    @PostMapping("/getByName")
    public ResponseEntity<LeaveTypeResponse> getLeaveTypeByName(@Valid @RequestBody NameRequest request) {
        return ResponseEntity.ok(leaveTypeService.getByName(request));
    }

    @Operation(summary = "Get annual leave types")
    @PostMapping("/getAnnual")
    public ResponseEntity<LeaveTypeResponseList> getAnnualLeaveTypes(@Valid @RequestBody BooleanRequest request) {
        return ResponseEntity.ok(leaveTypeService.getAnnual(request));
    }

    @Operation(summary = "Get unpaid leave types")
    @PostMapping("/getUnpaid")
    public ResponseEntity<LeaveTypeResponseList> getUnpaidLeaveTypes(@Valid @RequestBody BooleanRequest request) {
        return ResponseEntity.ok(leaveTypeService.getUnpaid(request));
    }

    @Operation(summary = "Get gender specific leave types")
    @PostMapping("/getGenderSpecific")
    public ResponseEntity<LeaveTypeResponseList> getGenderSpecificLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getGenderSpecific());
    }

    @Operation(summary = "Get leave types by borrowable limit")
    @PostMapping("/getByBorrowableLimit")
    public ResponseEntity<LeaveTypeResponseList> getLeaveTypesByBorrowableLimit(@Valid @RequestBody IntegerRequest request) {
        return ResponseEntity.ok(leaveTypeService.getByBorrowableLimit(request));
    }

    @Operation(summary = "Get leave types by valid after days")
    @PostMapping("/getByValidAfterDays")
    public ResponseEntity<LeaveTypeResponseList> getLeaveTypesByValidAfterDays(@Valid @RequestBody IntegerRequest request) {
        return ResponseEntity.ok(leaveTypeService.getByValidAfterDays(request));
    }
}
