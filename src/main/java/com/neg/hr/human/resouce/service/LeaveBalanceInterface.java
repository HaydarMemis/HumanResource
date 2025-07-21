package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.LeaveBalance;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface LeaveBalanceInterface {
    public LeaveBalance save(LeaveBalance leaveBalance);

    public LeaveBalance findById(Long id);

    public List<LeaveBalance> findAll();

    public void delete(Long id);

    public LeaveBalance update(Long id, LeaveBalance leaveBalance);
}
