package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.LeaveBalance;
import com.neg.hr.human.resouce.repository.LeaveBalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService implements LeaveBalanceInterface{
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveBalance save(LeaveBalance leaveBalance) {
        return leaveBalanceRepository.save(leaveBalance);
    }

    public LeaveBalance findById(Long id) {
        return leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    public List<LeaveBalance> findAll() {
        return leaveBalanceRepository.findAll();
    }

    public void delete(Long id) {
        leaveBalanceRepository.deleteById(id);
    }

    public LeaveBalance update(Long id, LeaveBalance leaveBalance) {
        LeaveBalance existing = findById(id);
        existing.setAmount(leaveBalance.getAmount());
        existing.setDate(leaveBalance.getDate());
        existing.setEmployee(leaveBalance.getEmployee());
        existing.setLeaveType(leaveBalance.getLeaveType());
        return leaveBalanceRepository.save(existing);
    }
}
