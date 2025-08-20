package com.neg.technology.human.resource.leave.controller;

import com.neg.technology.human.resource.leave.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponseList;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "LeavePolicy Controller", description = "Operations related to leave policies")
@RestController
@RequestMapping("/api/leave_policies")
@RequiredArgsConstructor
public class LeavePolicyController {

    private final LeavePolicyService leavePolicyService;

    @Operation(summary = "Get annual leave days")
    @ApiResponse(responseCode = "200", description = "Annual leave days calculated")
    @PostMapping("/annual-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> getAnnualLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getAnnualLeave(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get age-based leave bonus days")
    @PostMapping("/age-bonus")
    public Mono<ResponseEntity<LeavePolicyResponse>> getAgeBasedLeaveBonus(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getAgeBasedLeaveBonus(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Check birthday leave eligibility")
    @PostMapping("/birthday-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> checkBirthdayLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.checkBirthdayLeave(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get maternity leave days")
    @PostMapping("/maternity-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> getMaternityLeaveDays(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getMaternityLeaveDays(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get paternity leave days")
    @PostMapping("/paternity-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> getPaternityLeaveDays(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getPaternityLeaveDays(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Check if employee can borrow leave days")
    @PostMapping("/can-borrow-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> canBorrowLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.canBorrowLeave(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get bereavement leave days")
    @PostMapping("/bereavement-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> getBereavementLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getBereavementLeave(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get marriage leave days")
    @PostMapping("/marriage-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> getMarriageLeave(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getMarriageLeave(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Check military leave eligibility")
    @PostMapping("/military-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> getMilitaryLeaveInfo(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.getMilitaryLeaveInfo(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Check if given date is official holiday")
    @PostMapping("/holiday-leave")
    public Mono<ResponseEntity<LeavePolicyResponse>> isHoliday(
            @Valid @RequestBody LeavePolicyRequest request) {
        return leavePolicyService.isHoliday(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get all leave policies")
    @PostMapping("/all")
    public Mono<ResponseEntity<LeavePolicyResponseList>> getAllLeavePolicies() {
        return leavePolicyService.getAllLeavePolicies()
                .map(ResponseEntity::ok);
    }
}