package com.neg.technology.human.resource.leave.service;

import com.neg.technology.human.resource.employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.leave.model.request.ChangeLeaveRequestStatusRequest;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.response.ApprovedLeaveDatesResponseList;
import com.neg.technology.human.resource.leave.model.response.ChangeLeaveRequestStatusResponseList;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.StatusRequest;
import reactor.core.publisher.Mono;

public interface LeaveRequestService {
    Mono<LeaveRequestResponseList> getAll();

    Mono<LeaveRequestResponse> getById(IdRequest request);

    Mono<LeaveRequestResponse> create(CreateLeaveRequestRequest dto);

    Mono<LeaveRequestResponse> update(UpdateLeaveRequestRequest dto);

    Mono<Void> delete(IdRequest request);

    Mono<LeaveRequestResponseList> getByEmployee(IdRequest request);

    Mono<LeaveRequestResponseList> getByStatus(StatusRequest request);

    Mono<LeaveRequestResponseList> getCancelled();

    Mono<LeaveRequestResponseList> getByApprover(IdRequest request);

    Mono<LeaveRequestResponseList> getByEmployeeAndStatus(EmployeeStatusRequest request);

    Mono<LeaveRequestResponseList> getByDateRange(EmployeeDateRangeRequest request);

    Mono<LeaveRequestResponseList> getByEmployeeLeaveTypeAndDateRange(EmployeeLeaveTypeDateRangeRequest request);

    Mono<LeaveRequestResponseList> getOverlapping(EmployeeDateRangeRequest request);

    Mono<ApprovedLeaveDatesResponseList> getApprovedByEmployee(IdRequest request);

    Mono<ChangeLeaveRequestStatusResponseList> changeStatus(ChangeLeaveRequestStatusRequest request);

}