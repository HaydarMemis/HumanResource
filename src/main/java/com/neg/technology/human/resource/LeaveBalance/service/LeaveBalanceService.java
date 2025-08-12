package com.neg.technology.human.resource.LeaveBalance.service;

import com.neg.technology.human.resource.LeaveBalance.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.LeaveType.model.request.LeaveTypeYearRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface LeaveBalanceService {

    List<LeaveBalanceResponse> getAll();

    ResponseEntity<LeaveBalanceResponse> getById(IdRequest request);

    LeaveBalanceResponse create(CreateLeaveBalanceRequest request);

    ResponseEntity<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request);

    void delete(IdRequest request);

    List<LeaveBalanceResponse> getByEmployee(IdRequest request);

    List<LeaveBalanceResponse> getByEmployeeAndYear(EmployeeYearRequest request);

    ResponseEntity<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request);

    ResponseEntity<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request);

    List<LeaveBalanceResponse> getByLeaveTypeAndYear(LeaveTypeYearRequest request);
}
