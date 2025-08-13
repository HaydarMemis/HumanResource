package com.neg.technology.human.resource.LeaveType.service;

import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import com.neg.technology.human.resource.LeaveType.model.mapper.LeaveTypeMapper;
import com.neg.technology.human.resource.LeaveType.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.response.LeaveTypeResponse;
import com.neg.technology.human.resource.LeaveType.model.response.LeaveTypeResponseList;
import com.neg.technology.human.resource.LeaveType.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.Utility.request.BooleanRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.IntegerRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
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
        BusinessLogger.logCreated(LeaveType.class, saved.getId(), saved.getName());
        return leaveTypeMapper.toResponse(saved);
    }

    @Override
    public LeaveTypeResponse update(UpdateLeaveTypeRequest request) {
        LeaveType existing = leaveTypeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type", request.getId()));

        leaveTypeMapper.updateEntityFromRequest(request, existing);
        LeaveType updated = leaveTypeRepository.save(existing);
        BusinessLogger.logUpdated(LeaveType.class, updated.getId(), updated.getName());
        return leaveTypeMapper.toResponse(updated);
    }

    @Override
    public void delete(IdRequest request) {
        if (!leaveTypeRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("Leave Type", request.getId());
        }
        leaveTypeRepository.deleteById(request.getId());
        BusinessLogger.logDeleted(LeaveType.class, request.getId());
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
