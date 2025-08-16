package com.neg.technology.human.resource.leave_type.service;

import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave_type.model.entity.LeaveType;
import com.neg.technology.human.resource.leave_type.model.mapper.LeaveTypeMapper;
import com.neg.technology.human.resource.leave_type.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.leave_type.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.leave_type.model.response.LeaveTypeResponse;
import com.neg.technology.human.resource.leave_type.model.response.LeaveTypeResponseList;
import com.neg.technology.human.resource.leave_type.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.utility.module.entity.request.BooleanRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IntegerRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    @Override
    public LeaveTypeResponseList getAll() {
        List<LeaveType> entities = leaveTypeRepository.findAll();
        return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
    }

    @Override
    public LeaveTypeResponse getById(IdRequest request) {
        LeaveType entity = leaveTypeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type", request.getId()));
        return leaveTypeMapper.toResponse(entity);
    }

    @Override
    public LeaveTypeResponse create(CreateLeaveTypeRequest request) {
        LeaveType entity = leaveTypeMapper.toEntity(request);
        LeaveType saved = leaveTypeRepository.save(entity);
        Logger.logCreated(LeaveType.class, saved.getId(), saved.getName());
        return leaveTypeMapper.toResponse(saved);
    }

    @Override
    public LeaveTypeResponse update(UpdateLeaveTypeRequest request) {
        LeaveType existing = leaveTypeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type", request.getId()));

        leaveTypeMapper.updateEntityFromRequest(request, existing);
        LeaveType updated = leaveTypeRepository.save(existing);
        Logger.logUpdated(LeaveType.class, updated.getId(), updated.getName());
        return leaveTypeMapper.toResponse(updated);
    }

    @Override
    public void delete(IdRequest request) {
        if (!leaveTypeRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("Leave Type", request.getId());
        }
        leaveTypeRepository.deleteById(request.getId());
        Logger.logDeleted(LeaveType.class, request.getId());
    }

    @Override
    public LeaveTypeResponse getByName(NameRequest request) {
        LeaveType entity = leaveTypeRepository.findByName(request.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type with name", request.getName()));
        return leaveTypeMapper.toResponse(entity);
    }

    @Override
    public LeaveTypeResponseList getAnnual(BooleanRequest request) {
        List<LeaveType> entities = request.isValue()
                ? leaveTypeRepository.findByIsAnnualTrue()
                : leaveTypeRepository.findByIsAnnualFalse();
        return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
    }

    @Override
    public LeaveTypeResponseList getUnpaid(BooleanRequest request) {
        List<LeaveType> entities = request.isValue()
                ? leaveTypeRepository.findByIsUnpaidTrue()
                : leaveTypeRepository.findByIsUnpaidFalse();
        return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
    }

    @Override
    public LeaveTypeResponseList getGenderSpecific() {
        List<LeaveType> entities = leaveTypeRepository.findByGenderRequiredIsNotNull();
        return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
    }

    @Override
    public LeaveTypeResponseList getByBorrowableLimit(IntegerRequest request) {
        List<LeaveType> entities = leaveTypeRepository.findByBorrowableLimitGreaterThan(request.getValue());
        return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
    }

    @Override
    public LeaveTypeResponseList getByValidAfterDays(IntegerRequest request) {
        List<LeaveType> entities = leaveTypeRepository.findByValidAfterDaysGreaterThan(request.getValue());
        return new LeaveTypeResponseList(leaveTypeMapper.toResponseList(entities));
    }
}
