package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.mapper.LeaveBalanceMapper;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.leave.model.request.LeaveTypeYearRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {
    public static final String MESSAGE = "LeaveBalance";

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;

    public LeaveBalanceServiceImpl(LeaveBalanceRepository leaveBalanceRepository,
                                   EmployeeRepository employeeRepository,
                                   LeaveTypeRepository leaveTypeRepository,
                                   LeaveBalanceMapper leaveBalanceMapper) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceMapper = leaveBalanceMapper;
    }

    @Override
    public LeaveBalanceResponseList getAll() {
        return leaveBalanceMapper.toResponseList(leaveBalanceRepository.findAll());
    }

    @Override
    public ResponseEntity<LeaveBalanceResponse> getById(IdRequest request) {
        return leaveBalanceRepository.findById(request.getId())
                .map(leaveBalanceMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public LeaveBalanceResponse create(CreateLeaveBalanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

        LeaveBalance entity = leaveBalanceMapper.toEntity(request, employee, leaveType);
        LeaveBalance saved = leaveBalanceRepository.save(entity);
        Logger.logCreated(LeaveBalance.class, saved.getId(), MESSAGE);
        return leaveBalanceMapper.toResponse(saved);
    }

    @Override
    public ResponseEntity<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request) {
        LeaveBalance existing = leaveBalanceRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

        leaveBalanceMapper.updateEntity(existing, request, employee, leaveType);
        LeaveBalance updated = leaveBalanceRepository.save(existing);
        Logger.logUpdated(LeaveBalance.class, updated.getId(), MESSAGE);
        return ResponseEntity.ok(leaveBalanceMapper.toResponse(updated));
    }

    @Override
    public void delete(IdRequest request) {
        if (!leaveBalanceRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException(MESSAGE, request.getId());
        }
        leaveBalanceRepository.deleteById(request.getId());
        Logger.logDeleted(LeaveBalance.class, request.getId());
    }

    @Override
    public LeaveBalanceResponseList getByEmployee(IdRequest request) {
        return leaveBalanceMapper.toResponseList(
                leaveBalanceRepository.findByEmployeeId(request.getId())
        );
    }

    @Override
    public LeaveBalanceResponseList getByEmployeeAndYear(EmployeeYearRequest request) {
        return leaveBalanceMapper.toResponseList(
                leaveBalanceRepository.findByEmployeeIdAndDate(request.getYear(), request.getEmployeeId())
        );
    }

    @Override
    public ResponseEntity<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(
                        request.getEmployeeId(), request.getLeaveTypeId())
                .map(leaveBalanceMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        return leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndDate(
                        request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())
                .map(leaveBalanceMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public LeaveBalanceResponseList getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        return leaveBalanceMapper.toResponseList(
                leaveBalanceRepository.findByLeaveTypeIdAndDate(request.getLeaveTypeId(), request.getYear())
        );
    }
}
