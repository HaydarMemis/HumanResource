package com.neg.technology.human.resource.LeaveRequest.service;

import com.neg.technology.human.resource.LeaveRequest.model.request.*;
import com.neg.technology.human.resource.LeaveRequest.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.LeaveRequest.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.StatusRequest;
import com.neg.technology.human.resource.Employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.Employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeDateRangeRequest;

public interface LeaveRequestService {
    LeaveRequestResponseList getAll();

    LeaveRequestResponse getById(IdRequest request);

    LeaveRequestResponse create(CreateLeaveRequestRequest dto);

    LeaveRequestResponse update(UpdateLeaveRequestRequest dto);

    void delete(IdRequest request);

    LeaveRequestResponseList getByEmployee(IdRequest request);

    LeaveRequestResponseList getByStatus(StatusRequest request);

    LeaveRequestResponseList getCancelled();

    LeaveRequestResponseList getByApprover(IdRequest request);

    LeaveRequestResponseList getByEmployeeAndStatus(EmployeeStatusRequest request);

    LeaveRequestResponseList getByDateRange(EmployeeDateRangeRequest request);

    LeaveRequestResponseList getByEmployeeLeaveTypeAndDateRange(EmployeeLeaveTypeDateRangeRequest request);

    LeaveRequestResponseList getOverlapping(EmployeeDateRangeRequest request);
}
