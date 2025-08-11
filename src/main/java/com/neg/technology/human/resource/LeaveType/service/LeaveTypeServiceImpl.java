package com.neg.technology.human.resource.LeaveType.service;

import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.LeaveType.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    public Optional<LeaveType> findByName(String name) {
        return leaveTypeRepository.findByName(name);
    }

    @Override
    public List<LeaveType> findByIsAnnualTrue() {
        return leaveTypeRepository.findByIsAnnualTrue();
    }

    @Override
    public List<LeaveType> findByIsAnnualFalse() {
        return leaveTypeRepository.findByIsAnnualFalse();
    }

    @Override
    public List<LeaveType> findByIsUnpaidTrue() {
        return leaveTypeRepository.findByIsUnpaidTrue();
    }

    @Override
    public List<LeaveType> findByGenderRequiredTrue() {
       return leaveTypeRepository.findByGenderRequiredIsNotNull();
    }

    @Override
    public List<LeaveType> findByBorrowableLimitGreaterThan(Integer limit) {
        return leaveTypeRepository.findByBorrowableLimitGreaterThan(limit);
    }

    @Override
    public List<LeaveType> findByValidAfterDaysGreaterThan(Integer days) {
        return leaveTypeRepository.findByValidAfterDaysGreaterThan(days);
    }

    @Override
    public LeaveType save(LeaveType leaveType) {
        LeaveType saved = leaveTypeRepository.save(leaveType);
        BusinessLogger.logCreated(LeaveType.class, saved.getId(), saved.getName());
        return saved;
    }

    @Override
    public Optional<LeaveType> findById(Long id) {
        return leaveTypeRepository.findById(id);
    }

    @Override
    public List<LeaveType> findAll() {
        return leaveTypeRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave Type", id);
        }
        leaveTypeRepository.deleteById(id);
        BusinessLogger.logDeleted(LeaveType.class, id);
    }

    @Override
    public LeaveType update(Long id, LeaveType leaveType) {
        LeaveType existing = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type", id));

        existing.setName(leaveType.getName());
        existing.setBorrowableLimit(leaveType.getBorrowableLimit());
        existing.setValidAfterDays(leaveType.getValidAfterDays());
        existing.setIsAnnual(leaveType.getIsAnnual());
        existing.setDefaultDays(leaveType.getDefaultDays());
        existing.setIsUnpaid(leaveType.getIsUnpaid());

        existing.setGenderRequired(leaveType.getGenderRequired());

        existing.setResetPeriod(leaveType.getResetPeriod());
        existing.setValidUntilDays(leaveType.getValidUntilDays());

        LeaveType updated = leaveTypeRepository.save(existing);
        BusinessLogger.logUpdated(LeaveType.class, updated.getId(), updated.getName());
        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return leaveTypeRepository.existsById(id);
    }

    @Override
    public List<LeaveType> findByIsUnpaidFalse() {
        return leaveTypeRepository.findByIsUnpaidFalse();
    }
}
