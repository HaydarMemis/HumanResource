package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
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
    public Mono<LeaveBalanceResponse> getById(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id is required"));
        }
        return Mono.fromCallable(() -> leaveBalanceRepository.findById(request.getId())
                .map(leaveBalanceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Request cannot be null"));
        }

        return Mono.fromCallable(() -> {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

            LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

            leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                    request.getEmployeeId(),
                    request.getLeaveTypeId(),
                    request.getYear()).ifPresent(existing -> {
                        throw new IllegalArgumentException(
                                "Leave balance already exists for this employee, leave type and year. Use update method instead.");
                    });

            BigDecimal requestedDays = request.getTotalDays() != null ? request.getTotalDays()
                    : (leaveType.getDefaultDays() != null ? BigDecimal.valueOf(leaveType.getDefaultDays())
                            : BigDecimal.ZERO);

            // --- Kırpılmış izin miktarını al ---
            BigDecimal totalDays = leaveBalanceValidator.getCappedLeaveAmount(requestedDays, employee, leaveType);

            LeaveBalance entity = LeaveBalance.builder()
                    .employee(employee)
                    .leaveType(leaveType)
                    .year(request.getYear())
                    .totalDays(totalDays)
                    .usedDays(BigDecimal.ZERO)
                    .build();

            LeaveBalance saved = leaveBalanceRepository.save(entity);
            Logger.logCreated(LeaveBalance.class, saved.getId(), MESSAGE);
            return leaveBalanceMapper.toResponse(saved);
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

        return Mono.fromCallable(() -> {
            if (!leaveBalanceRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException(MESSAGE, request.getId());
            }
            leaveBalanceRepository.deleteById(request.getId());
            Logger.logDeleted(LeaveBalance.class, request.getId());
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployee(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId cannot be null"));
        }

        return Mono.fromCallable(
                () -> leaveBalanceMapper.toResponseList(leaveBalanceRepository.findByEmployeeId(request.getId())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and Year are required"));
        }

        return Mono.fromCallable(() -> leaveBalanceMapper.toResponseList(
                leaveBalanceRepository.findByEmployeeIdAndYear(request.getEmployeeId(), request.getYear())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and LeaveTypeId are required"));
        }

        return Mono.fromCallable(() -> leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(request.getEmployeeId(), request.getLeaveTypeId())
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                        "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId())))
                .map(leaveBalanceMapper::toResponse)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null
                || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId and Year are required"));
        }

        return Mono.fromCallable(() -> leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(request.getEmployeeId(), request.getLeaveTypeId(),
                        request.getYear())
                .map(leaveBalanceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                        "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId() +
                                ", Year: " + request.getYear())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        if (request == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("LeaveTypeId and Year are required"));
        }

        return Mono.fromCallable(() -> leaveBalanceMapper.toResponseList(
                leaveBalanceRepository.findByLeaveTypeIdAndYear(request.getLeaveTypeId(), request.getYear())))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deductLeave(DeductLeaveRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null
                || request.getYear() == null || request.getAmount() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId, Year and Amount are required"));
        }

        return Mono.fromRunnable(() -> {
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(request.getEmployeeId(),
                            request.getLeaveTypeId(),
                            request.getYear())
                    .orElseThrow(() -> new ResourceNotFoundException("LeaveBalance",
                            "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId() +
                                    ", Year: " + request.getYear()));

            leaveBalanceValidator.hasEnoughBalance(balance.getAvailableBalance(), request.getAmount());
            balance.deduct(request.getAmount());
            leaveBalanceRepository.save(balance);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<Void> addLeave(AddLeaveRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null
                || request.getYear() == null || request.getAmount() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId, Year and Amount are required"));
        }

        return Mono.zip(
                Mono.fromCallable(() -> employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()))),
                Mono.fromCallable(() -> leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()))))
                .flatMap(tuple -> {
                    Employee employee = tuple.getT1();
                    LeaveType leaveType = tuple.getT2();

                    LeaveBalance balance = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdAndYear(request.getEmployeeId(), request.getLeaveTypeId(),
                                    request.getYear())
                            .orElseGet(() -> LeaveBalance.builder()
                                    .employee(employee)
                                    .leaveType(leaveType)
                                    .year(request.getYear())
                                    .totalDays(BigDecimal.ZERO)
                                    .usedDays(BigDecimal.ZERO)
                                    .build());

                    // --- İzin miktarını kırp ---
                    BigDecimal cappedAmount = leaveBalanceValidator.getCappedLeaveAmount(request.getAmount(), employee,
                            leaveType);

                    // Geçmiş yıllardan devreden izinleri ekle
                    BigDecimal carryOver = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                            .stream()
                            .filter(b -> b.getYear() < request.getYear())
                            .map(LeaveBalance::getAvailableBalance)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    balance.setTotalDays(balance.getTotalDays().add(cappedAmount).add(carryOver));

                    leaveBalanceRepository.save(balance);

                    return Mono.empty();
                }).subscribeOn(Schedulers.boundedElastic()).then();
    }

}