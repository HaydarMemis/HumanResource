package com.neg.technology.human.resource.leave.service;

import com.neg.technology.human.resource.leave.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.leave.model.response.LeavePolicyResponseList;
import reactor.core.publisher.Mono;

public interface LeavePolicyService {

    Mono<LeavePolicyResponse> getAnnualLeave(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> getAgeBasedLeaveBonus(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> checkBirthdayLeave(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> getMaternityLeaveDays(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> getPaternityLeaveDays(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> canBorrowLeave(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> getBereavementLeave(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> getMarriageLeave(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> getMilitaryLeaveInfo(LeavePolicyRequest request);

    Mono<LeavePolicyResponse> isHoliday(LeavePolicyRequest request);

    Mono<LeavePolicyResponseList> getAllLeavePolicies();
}