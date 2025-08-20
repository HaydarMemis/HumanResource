package com.neg.technology.human.resource.leave.service;

import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.leave.model.request.LeaveTypeYearRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import reactor.core.publisher.Mono;

public interface LeaveBalanceService {

    Mono<LeaveBalanceResponseList> getAll();

    Mono<LeaveBalanceResponse> getById(IdRequest request);

    Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request);

    Mono<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request);

    Mono<Void> delete(IdRequest request);

    Mono<LeaveBalanceResponseList> getByEmployee(IdRequest request);

    Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request);

    Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request);

    Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request);

    Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request);
}