package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.leave.service.LeaveTypeService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.mapper.LeaveTypeMapper;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveTypeResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveTypeResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.utility.module.entity.request.BooleanRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IntegerRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    @Override
    public Mono<LeaveTypeResponseList> getAll() {
        return Mono.fromCallable(() -> {
            List<LeaveType> entities = leaveTypeRepository.findAll();
            return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
        });
    }

    @Override
    public Mono<LeaveTypeResponse> getById(IdRequest request) {
        return Mono.fromCallable(() ->
                leaveTypeRepository.findById(request.getId())
                        .map(leaveTypeMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave Type", request.getId()))
        );
    }

    @Override
    public Mono<LeaveTypeResponse> create(CreateLeaveTypeRequest request) {
        return Mono.fromCallable(() -> {
            LeaveType entity = leaveTypeMapper.toEntity(request);
            LeaveType saved = leaveTypeRepository.save(entity);
            Logger.logCreated(LeaveType.class, saved.getId(), saved.getName());
            return leaveTypeMapper.toResponse(saved);
        });
    }

    @Override
    public Mono<LeaveTypeResponse> update(UpdateLeaveTypeRequest request) {
        return Mono.fromCallable(() -> {
            LeaveType existing = leaveTypeRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave Type", request.getId()));

            leaveTypeMapper.updateEntityFromRequest(request, existing);
            LeaveType updated = leaveTypeRepository.save(existing);
            Logger.logUpdated(LeaveType.class, updated.getId(), updated.getName());
            return leaveTypeMapper.toResponse(updated);
        });
    }

    @Override
    public Mono<Void> delete(IdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!leaveTypeRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException("Leave Type", request.getId());
            }
            leaveTypeRepository.deleteById(request.getId());
            Logger.logDeleted(LeaveType.class, request.getId());
        });
    }

    @Override
    public Mono<LeaveTypeResponse> getByName(NameRequest request) {
        return Mono.fromCallable(() ->
                leaveTypeRepository.findByName(request.getName())
                        .map(leaveTypeMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave Type with name", request.getName()))
        );
    }

    @Override
    public Mono<LeaveTypeResponseList> getAnnual(BooleanRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveType> entities = request.isValue()
                    ? leaveTypeRepository.findByIsAnnualTrue()
                    : leaveTypeRepository.findByIsAnnualFalse();
            return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
        });
    }

    @Override
    public Mono<LeaveTypeResponseList> getUnpaid(BooleanRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveType> entities = request.isValue()
                    ? leaveTypeRepository.findByIsUnpaidTrue()
                    : leaveTypeRepository.findByIsUnpaidFalse();
            return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
        });
    }

    @Override
    public Mono<LeaveTypeResponseList> getGenderSpecific() {
        return Mono.fromCallable(() -> {
            List<LeaveType> entities = leaveTypeRepository.findByGenderRequiredIsNotNull();
            return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
        });
    }

    @Override
    public Mono<LeaveTypeResponseList> getByBorrowableLimit(IntegerRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveType> entities = leaveTypeRepository.findByBorrowableLimitGreaterThan(request.getValue());
            return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
        });
    }

    @Override
    public Mono<LeaveTypeResponseList> getByValidAfterDays(IntegerRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveType> entities = leaveTypeRepository.findByValidAfterDaysGreaterThan(request.getValue());
            return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
        });
    }
}