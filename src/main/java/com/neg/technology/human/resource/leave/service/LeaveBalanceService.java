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
import org.springframework.http.ResponseEntity;

public interface LeaveBalanceService {

    LeaveBalanceResponseList getAll();

    ResponseEntity<LeaveBalanceResponse> getById(IdRequest request);

    LeaveBalanceResponse create(CreateLeaveBalanceRequest request);

    ResponseEntity<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request);

    void delete(IdRequest request);

    LeaveBalanceResponseList getByEmployee(IdRequest request);

    LeaveBalanceResponseList getByEmployeeAndYear(EmployeeYearRequest request);

    ResponseEntity<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request);

    ResponseEntity<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request);

    LeaveBalanceResponseList getByLeaveTypeAndYear(LeaveTypeYearRequest request);
}
