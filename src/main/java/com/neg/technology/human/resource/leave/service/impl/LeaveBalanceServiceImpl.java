package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.mapper.LeaveBalanceMapper;
import com.neg.technology.human.resource.leave.model.request.*;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.leave.validator.LeaveBalanceValidator;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    public static final String MESSAGE = "LeaveBalance";

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final LeaveBalanceValidator leaveBalanceValidator;

    @Override
    public Mono<LeaveBalanceResponseList> getAll() {
        return Mono.fromCallable(() -> {
            List<LeaveBalance> balances = leaveBalanceRepository.findAll();
            List<LeaveBalanceResponse> responses = balances.stream()
                    .map(leaveBalanceMapper::toResponse)
                    .collect(Collectors.toList());
            return new LeaveBalanceResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        if (request == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("LeaveTypeId is required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByLeaveTypeId(request.getLeaveTypeId())
                )
        ).subscribeOn(Schedulers.boundedElastic());
    }



    @Override
    public Mono<LeaveBalanceResponse> getById(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id is required"));
        }
        return Mono.fromCallable(() ->
                leaveBalanceRepository.findById(request.getId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        BigDecimal totalAmount = request.getTotalAmount() != null ? request.getTotalAmount() : BigDecimal.ZERO;

        return Mono.zip(
                Mono.fromCallable(() -> employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()))),
                Mono.fromCallable(() -> leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId())))
        ).flatMap(tuple -> {
            Employee employee = tuple.getT1();
            LeaveType leaveType = tuple.getT2();

            leaveBalanceValidator.validateLeaveCreation(totalAmount, employee, leaveType);

            return Mono.fromCallable(() -> leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(
                    request.getEmployeeId(), request.getLeaveTypeId()
            )).flatMap(existingBalanceOpt -> {
                if (existingBalanceOpt.isPresent()) {
                    return Mono.error(new IllegalArgumentException(
                            "Leave balance for this employee and leave type already exists. Use update method instead."));
                }

                LeaveBalance entity = LeaveBalance.builder()
                        .employee(employee)
                        .leaveType(leaveType)
                        .totalAmount(totalAmount)
                        .usedDays(0)
                        .build();

                LeaveBalance saved = leaveBalanceRepository.save(entity);
                Logger.logCreated(LeaveBalance.class, saved.getId(), MESSAGE);
                return Mono.just(leaveBalanceMapper.toResponse(saved));
            });
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id is required for update"));
        }

        return Mono.fromCallable(() -> {
            LeaveBalance existing = leaveBalanceRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

            Employee employee = null;
            if (request.getEmployeeId() != null) {
                employee = employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));
            }

            LeaveType leaveType = null;
            if (request.getLeaveTypeId() != null) {
                leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));
            }

            leaveBalanceMapper.updateEntity(existing, request, employee, leaveType);

            if (request.getTotalAmount() != null) {
                existing.setTotalAmount(request.getTotalAmount());
            }

            LeaveBalance updated = leaveBalanceRepository.save(existing);
            Logger.logUpdated(LeaveBalance.class, updated.getId(), MESSAGE);
            return leaveBalanceMapper.toResponse(updated);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id cannot be null"));
        }

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
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId cannot be null"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(leaveBalanceRepository.findByEmployeeId(request.getId()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request) {
        if (request == null || request.getEmployeeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId is required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByEmployeeId(request.getEmployeeId())
                )
        ).subscribeOn(Schedulers.boundedElastic());
    }



    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        return Mono.fromCallable(() -> leaveBalanceRepository
                        .findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, -1L)))
                .map(leaveBalanceMapper::toResponse)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and LeaveTypeId are required"));
        }

        return Mono.fromCallable(() -> {
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, -1L));

            return leaveBalanceMapper.toResponse(balance);
        }).subscribeOn(Schedulers.boundedElastic());
    }



    @Override
    public Mono<Void> deductLeave(DeductLeaveRequest request) {
        return Mono.fromRunnable(() -> {
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, -1L));

            BigDecimal available = balance.getAvailableBalance();
            if (available.compareTo(request.getDays()) < 0) {
                throw new IllegalArgumentException("Insufficient leave balance");
            }

            balance.deduct(request.getDays());
            leaveBalanceRepository.save(balance);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> addLeave(AddLeaveRequest request) {
        return Mono.fromRunnable(() -> {
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                    .orElseGet(() -> {
                        Employee employee = employeeRepository.findById(request.getEmployeeId())
                                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));
                        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                                .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));
                        LeaveBalance newBalance = LeaveBalance.builder()
                                .employee(employee)
                                .leaveType(leaveType)
                                .totalAmount(BigDecimal.ZERO)
                                .usedDays(0)
                                .build();
                        leaveBalanceRepository.save(newBalance);
                        return newBalance;
                    });

            balance.addLeave(request.getDays());
            leaveBalanceRepository.save(balance);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
