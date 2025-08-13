package com.neg.technology.human.resource.LeaveType.service;

import com.neg.technology.human.resource.LeaveType.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.response.LeaveTypeResponse;
import com.neg.technology.human.resource.LeaveType.model.response.LeaveTypeResponseList;
import com.neg.technology.human.resource.Utility.request.BooleanRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.IntegerRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;

public interface LeaveTypeService {

    LeaveTypeResponseList getAll();

    LeaveTypeResponse getById(IdRequest request);

    LeaveTypeResponse create(CreateLeaveTypeRequest request);

    LeaveTypeResponse update(UpdateLeaveTypeRequest request);

    void delete(IdRequest request);

    LeaveTypeResponse getByName(NameRequest request);

    LeaveTypeResponseList getAnnual(BooleanRequest request);

    LeaveTypeResponseList getUnpaid(BooleanRequest request);

    LeaveTypeResponseList getGenderSpecific();

    LeaveTypeResponseList getByBorrowableLimit(IntegerRequest request);

    LeaveTypeResponseList getByValidAfterDays(IntegerRequest request);
}
