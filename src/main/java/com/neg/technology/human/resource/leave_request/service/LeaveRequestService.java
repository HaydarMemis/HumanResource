package com.neg.technology.human.resource.leave_request.service;

import com.neg.technology.human.resource.leave_request.model.request.*;
import com.neg.technology.human.resource.leave_request.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.leave_request.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.StatusRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.leave_type.model.request.EmployeeLeaveTypeDateRangeRequest;

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
