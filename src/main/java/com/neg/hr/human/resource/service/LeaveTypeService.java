package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveTypeService implements LeaveTypeInterface {

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
        return leaveTypeRepository.findByGenderRequiredTrue();
    }

    @Override
    public List<LeaveType> findByBorrowableLimitGreaterThan(Integer limit) {
        return leaveTypeRepository.findByBorrowableLimitGreaterThan(limit);
    }

    @Override
    public List<LeaveType> findByValidAfterDaysGreaterThan(Integer days) {
        return leaveTypeRepository.findByValidAfterDaysGreaterThan(days);
    }

    public LeaveType save(LeaveType leaveType) {
        return leaveTypeRepository.save(leaveType);
    }

    public LeaveType findById(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    public List<LeaveType> findAll() {
        return leaveTypeRepository.findAll();
    }

    public void delete(Long id) {
        leaveTypeRepository.deleteById(id);
    }

    public LeaveType update(Long id, LeaveType leaveType) {
        LeaveType existing = findById(id);
        existing.setName(leaveType.getName());
        existing.setBorrowableLimit(leaveType.getBorrowableLimit());
        existing.setValidAfterDays(leaveType.getValidAfterDays());
        existing.setIsAnnual(leaveType.getIsAnnual());
        existing.setDefaultDays(leaveType.getDefaultDays());
        existing.setIsUnpaid(leaveType.getIsUnpaid());
        existing.setGenderRequired(leaveType.getGenderRequired());
        existing.setResetPeriod(leaveType.getResetPeriod());
        existing.setValidUntilDays(leaveType.getValidUntilDays());
        return leaveTypeRepository.save(existing);
    }
}
