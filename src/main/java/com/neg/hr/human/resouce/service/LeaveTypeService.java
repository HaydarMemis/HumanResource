package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.LeaveType;
import com.neg.hr.human.resouce.repository.LeaveTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;

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
