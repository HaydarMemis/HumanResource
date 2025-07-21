package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.LeaveType;

import java.util.List;

public interface LeaveTypeInterface {

    public LeaveType save(LeaveType leaveType);

    public LeaveType findById(Long id);

    public List<LeaveType> findAll();

    public void delete(Long id);

    public LeaveType update(Long id, LeaveType leaveType);
}
