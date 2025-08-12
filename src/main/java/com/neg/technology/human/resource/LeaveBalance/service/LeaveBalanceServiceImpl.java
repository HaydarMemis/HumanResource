package com.neg.technology.human.resource.LeaveBalance.service;

import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.LeaveBalance.model.entity.LeaveBalance;
import com.neg.technology.human.resource.LeaveBalance.model.mapper.LeaveBalanceMapper;
import com.neg.technology.human.resource.LeaveBalance.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.LeaveBalance.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.LeaveBalance.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import com.neg.technology.human.resource.LeaveType.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.LeaveType.model.request.LeaveTypeYearRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

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
    public List<LeaveBalanceResponse> getAll() {
        return (List<LeaveBalanceResponse>) leaveBalanceMapper.toResponseList(leaveBalanceRepository.findAll());
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
        BusinessLogger.logCreated(LeaveBalance.class, saved.getId(), "LeaveBalance");
        return leaveBalanceMapper.toResponse(saved);
    }

    @Override
    public ResponseEntity<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request) {
        LeaveBalance existing = leaveBalanceRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveBalance", request.getId()));

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

        leaveBalanceMapper.updateEntity(existing, request, employee, leaveType);
        LeaveBalance updated = leaveBalanceRepository.save(existing);
        BusinessLogger.logUpdated(LeaveBalance.class, updated.getId(), "LeaveBalance");
        return ResponseEntity.ok(leaveBalanceMapper.toResponse(updated));
    }

    @Override
    public void delete(IdRequest request) {
        if (!leaveBalanceRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("LeaveBalance", request.getId());
        }
        leaveBalanceRepository.deleteById(request.getId());
        BusinessLogger.logDeleted(LeaveBalance.class, request.getId());
    }

    @Override
    public List<LeaveBalanceResponse> getByEmployee(IdRequest request) {
        return (List<LeaveBalanceResponse>) leaveBalanceMapper.toResponseList(
                leaveBalanceRepository.findByEmployeeId(request.getId())
        );
    }

    @Override
    public List<LeaveBalanceResponse> getByEmployeeAndYear(EmployeeYearRequest request) {
        return (List<LeaveBalanceResponse>) leaveBalanceMapper.toResponseList(
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
    public List<LeaveBalanceResponse> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        return (List<LeaveBalanceResponse>) leaveBalanceMapper.toResponseList(
                leaveBalanceRepository.findByLeaveTypeIdAndDate(request.getLeaveTypeId(), request.getYear())
        );
    }
}
