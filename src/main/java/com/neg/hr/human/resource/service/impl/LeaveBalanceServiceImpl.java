package com.neg.hr.human.resource.service.impl;

import com.neg.hr.human.resource.business.BusinessLogger;
import com.neg.hr.human.resource.entity.LeaveBalance;
import com.neg.hr.human.resource.exception.ResourceNotFoundException;
import com.neg.hr.human.resource.repository.LeaveBalanceRepository;
import com.neg.hr.human.resource.service.LeaveBalanceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveBalanceServiceImpl(LeaveBalanceRepository leaveBalanceRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    @Override
    public List<LeaveBalance> findByEmployeeId(Long employeeId) {
        return leaveBalanceRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<LeaveBalance> findByEmployeeIdAndDate(Integer year, Long employeeId) {
        return leaveBalanceRepository.findByEmployeeIdAndDate(year, employeeId);
    }

    @Override
    public Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeId(Long employeeId, Long leaveTypeId) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId);
    }

    @Override
    public Optional<LeaveBalance> findByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndDate(employeeId, leaveTypeId, year);
    }

    @Override
    public List<LeaveBalance> findByLeaveTypeIdAndDate(Long leaveTypeId, Integer year) {
        return leaveBalanceRepository.findByLeaveTypeIdAndDate(leaveTypeId, year);
    }

    @Override
    public boolean existsByEmployeeIdAndLeaveTypeIdAndDate(Long employeeId, Long leaveTypeId, Integer year) {
        return leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeIdAndDate(employeeId, leaveTypeId, year);
    }

    @Override
    public LeaveBalance save(LeaveBalance leaveBalance) {
        LeaveBalance saved = leaveBalanceRepository.save(leaveBalance);
        BusinessLogger.logCreated(LeaveBalance.class, saved.getId(), "LeaveBalance");
        return saved;
    }

    @Override
    public List<LeaveBalance> findAll() {
        return leaveBalanceRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        if(!leaveBalanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave Balance", id);
        }
        leaveBalanceRepository.deleteById(id);
        BusinessLogger.logDeleted(LeaveBalance.class, id);
    }

    @Override
    public LeaveBalance update(Long id, LeaveBalance leaveBalance) {
        LeaveBalance existing = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave Balance", id));

        existing.setEmployee(leaveBalance.getEmployee());
        existing.setLeaveType(leaveBalance.getLeaveType());
        existing.setDate(leaveBalance.getDate());
        existing.setAmount(leaveBalance.getAmount());

        LeaveBalance updated = leaveBalanceRepository.save(existing);
        BusinessLogger.logUpdated(LeaveBalance.class, updated.getId(), "LeaveBalance");
        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return leaveBalanceRepository.existsById(id);
    }

    @Override
    public Optional<LeaveBalance> findById(Long id) {
        return leaveBalanceRepository.findById(id);
    }
}
