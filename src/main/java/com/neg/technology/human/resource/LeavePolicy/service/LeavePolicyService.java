package com.neg.technology.human.resource.LeavePolicy.service;

import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.LeavePolicy.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.LeavePolicy.model.response.LeavePolicyResponse;
import com.neg.technology.human.resource.LeavePolicy.model.response.LeavePolicyResponseList;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface LeavePolicyService {

    ResponseEntity<LeavePolicyResponse> getAnnualLeave(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> getAgeBasedLeaveBonus(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> checkBirthdayLeave(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> getMaternityLeaveDays(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> getPaternityLeaveDays(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> canBorrowLeave(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> getBereavementLeave(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> getMarriageLeave(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> getMilitaryLeaveInfo(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponse> isHoliday(LeavePolicyRequest request);

    ResponseEntity<LeavePolicyResponseList> getAllLeavePolicies();

    Optional<Employee> getEmployeeEntityById(Long id);

    Optional<Employee> findById(Long id);
}
