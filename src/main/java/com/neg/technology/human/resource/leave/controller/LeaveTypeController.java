package com.neg.technology.human.resource.leave.controller;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveTypeResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveTypeResponseList;
import com.neg.technology.human.resource.leave.service.LeaveTypeService;
import com.neg.technology.human.resource.leave.validator.LeaveTypeValidator;
import com.neg.technology.human.resource.utility.module.entity.request.BooleanRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IntegerRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "LeaveType Controller", description = "Operations related to leave type management")
@RestController
@RequestMapping("/api/leave-types")
@RequiredArgsConstructor
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;
    private final LeaveTypeValidator leaveTypeValidator;

    @Operation(summary = "Get all leave types")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<LeaveTypeResponseList>> getAllLeaveTypes() {
        return leaveTypeService.getAll()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave type by ID")
    @ApiResponse(responseCode = "200", description = "Leave type found")
    @ApiResponse(responseCode = "404", description = "Leave type not found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<LeaveTypeResponse>> getLeaveTypeById(@Valid @RequestBody IdRequest request) {
        return leaveTypeService.getById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Create new leave type")
    @PostMapping("/create")
    public Mono<ResponseEntity<LeaveTypeResponse>> createLeaveType(@Valid @RequestBody CreateLeaveTypeRequest dto) {
        leaveTypeValidator.validateCreate(dto);
        return leaveTypeService.create(dto)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update existing leave type")
    @PostMapping("/update")
    public Mono<ResponseEntity<LeaveTypeResponse>> updateLeaveType(@Valid @RequestBody UpdateLeaveTypeRequest dto) {
        leaveTypeValidator.validateUpdate(dto);
        return leaveTypeService.update(dto)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete leave type")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deleteLeaveType(@Valid @RequestBody IdRequest request) {
        return leaveTypeService.delete(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get leave type by name")
    @PostMapping("/getByName")
    public Mono<ResponseEntity<LeaveTypeResponse>> getLeaveTypeByName(@Valid @RequestBody NameRequest request) {
        return leaveTypeService.getByName(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get annual leave types")
    @PostMapping("/getAnnual")
    public Mono<ResponseEntity<LeaveTypeResponseList>> getAnnualLeaveTypes(@Valid @RequestBody BooleanRequest request) {
        return leaveTypeService.getAnnual(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get unpaid leave types")
    @PostMapping("/getUnpaid")
    public Mono<ResponseEntity<LeaveTypeResponseList>> getUnpaidLeaveTypes(@Valid @RequestBody BooleanRequest request) {
        return leaveTypeService.getUnpaid(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get gender specific leave types")
    @PostMapping("/getGenderSpecific")
    public Mono<ResponseEntity<LeaveTypeResponseList>> getGenderSpecificLeaveTypes() {
        return leaveTypeService.getGenderSpecific()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave types by borrowable limit")
    @PostMapping("/getByBorrowableLimit")
    public Mono<ResponseEntity<LeaveTypeResponseList>> getLeaveTypesByBorrowableLimit(@Valid @RequestBody IntegerRequest request) {
        return leaveTypeService.getByBorrowableLimit(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get leave types by valid after days")
    @PostMapping("/getByValidAfterDays")
    public Mono<ResponseEntity<LeaveTypeResponseList>> getLeaveTypesByValidAfterDays(@Valid @RequestBody IntegerRequest request) {
        return leaveTypeService.getByValidAfterDays(request)
                .map(ResponseEntity::ok);
    }
}