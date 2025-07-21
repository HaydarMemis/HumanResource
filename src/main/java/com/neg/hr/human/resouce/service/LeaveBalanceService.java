package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.LeaveBalance;
import com.neg.hr.human.resouce.repository.LeaveBalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeaveBalanceService implements LeaveBalanceInterface {

    private final LeaveBalanceRepository leaveBalanceRepository;

    // Constructor injection
    public LeaveBalanceService(LeaveBalanceRepository leaveBalanceRepository) {
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
        return leaveBalanceRepository.save(leaveBalance);
    }

    @Override
    public List<LeaveBalance> findAll() {
        return leaveBalanceRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        leaveBalanceRepository.deleteById(id);
    }

    @Override
    public LeaveBalance update(Long id, LeaveBalance leaveBalance) {
        LeaveBalance existing = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LeaveBalance not found with id " + id));

        existing.setEmployee(leaveBalance.getEmployee());
        existing.setLeaveType(leaveBalance.getLeaveType());
        existing.setDate(leaveBalance.getDate());
        existing.setAmount(leaveBalance.getAmount());

        return leaveBalanceRepository.save(existing);
    }

    @Override
    public Optional<LeaveBalance> findById(Long id) {
        return leaveBalanceRepository.findById(id);
    }
}
