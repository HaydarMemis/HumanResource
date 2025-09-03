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
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {
    public static final String MESSAGE = "LeaveBalance";

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;

    @Override
    public Mono<LeaveBalanceResponseList> getAll() {
        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(leaveBalanceRepository.findAll())
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getById(IdRequest request) {
        return Mono.fromCallable(() ->
                leaveBalanceRepository.findById(request.getId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        return Mono.fromCallable(() -> {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

            LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

            LeaveBalance existing = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(
                    request.getEmployeeId(), request.getLeaveTypeId()
            ).orElse(null);

            LeaveBalance entity;
            if (existing != null) {
                existing.setAmount(request.getAmount());
                entity = leaveBalanceRepository.save(existing);
                Logger.logUpdated(LeaveBalance.class, entity.getId(), MESSAGE);
            } else {
                entity = leaveBalanceMapper.toEntity(request, employee, leaveType);
                entity = leaveBalanceRepository.save(entity);
                Logger.logCreated(LeaveBalance.class, entity.getId(), MESSAGE);
            }

            return leaveBalanceMapper.toResponse(entity);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request) {
        return Mono.fromCallable(() -> {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

            LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

            LeaveBalance existing = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(
                    request.getEmployeeId(), request.getLeaveTypeId()
            ).orElseThrow(() -> new ResourceNotFoundException(
                    MESSAGE, "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId()
            ));

            existing.setAmount(request.getAmount());
            LeaveBalance updated = leaveBalanceRepository.save(existing);
            Logger.logUpdated(LeaveBalance.class, updated.getId(), MESSAGE);
            return leaveBalanceMapper.toResponse(updated);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(IdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!leaveBalanceRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException(MESSAGE, request.getId());
            }
            leaveBalanceRepository.deleteById(request.getId());
            Logger.logDeleted(LeaveBalance.class, request.getId());
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployee(IdRequest request) {
        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByEmployeeId(request.getId())
                )
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        return Mono.fromCallable(() ->
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(
                                request.getEmployeeId(), request.getLeaveTypeId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId()))
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
