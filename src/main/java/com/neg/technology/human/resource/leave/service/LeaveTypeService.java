package com.neg.technology.human.resource.leave.service;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveTypeResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveTypeResponseList;
import com.neg.technology.human.resource.utility.module.entity.request.BooleanRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IntegerRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import reactor.core.publisher.Mono;

public interface LeaveTypeService {

    Mono<LeaveTypeResponseList> getAll();

    Mono<LeaveTypeResponse> getById(IdRequest request);

    Mono<LeaveTypeResponse> create(CreateLeaveTypeRequest request);

    Mono<LeaveTypeResponse> update(UpdateLeaveTypeRequest request);

    Mono<Void> delete(IdRequest request);

    Mono<LeaveTypeResponse> getByName(NameRequest request);

    Mono<LeaveTypeResponseList> getAnnual(BooleanRequest request);

    Mono<LeaveTypeResponseList> getUnpaid(BooleanRequest request);

    Mono<LeaveTypeResponseList> getGenderSpecific();

    Mono<LeaveTypeResponseList> getByBorrowableLimit(IntegerRequest request);

    Mono<LeaveTypeResponseList> getByValidAfterDays(IntegerRequest request);
}