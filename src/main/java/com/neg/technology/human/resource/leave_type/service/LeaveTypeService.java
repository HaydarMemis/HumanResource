package com.neg.technology.human.resource.leave_type.service;

import com.neg.technology.human.resource.leave_type.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.leave_type.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.leave_type.model.response.LeaveTypeResponse;
import com.neg.technology.human.resource.leave_type.model.response.LeaveTypeResponseList;
import com.neg.technology.human.resource.utility.module.entity.request.BooleanRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IntegerRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;

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
