package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.mapper.LeaveBalanceMapper;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.request.LeavePolicyRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.leave.model.request.LeaveTypeYearRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    public static final String MESSAGE = "LeaveBalance";

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final LeavePolicyService leavePolicyService;

    @Override
    public Mono<LeaveBalanceResponseList> getAll() {
        return Mono.fromCallable(() -> {
                    List<LeaveBalance> allBalances = leaveBalanceRepository.findAll();
                    return leaveBalanceMapper.toResponseList(allBalances);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getById(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id is required"));
        }

        return Mono.fromCallable(() -> {
                    LeaveBalance lb = leaveBalanceRepository.findById(request.getId())
                            .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

                    List<LeaveBalance> allBalances = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(lb.getEmployee().getId(), lb.getLeaveType().getId());

                    return leaveBalanceMapper.toResponse(lb, allBalances);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId ve LeaveTypeId zorunlu"));
        }

        return Mono.fromCallable(() -> {
                    // Employee ve LeaveType validation
                    Employee employee = employeeRepository.findById(request.getEmployeeId())
                            .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

                    LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                            .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

                    String leaveName = leaveType.getName() != null ? leaveType.getName().trim().toLowerCase() : "";

                    int requestedDays = request.getAmount() != null ? request.getAmount().intValue() : 0;
                    if (requestedDays <= 0) {
                        throw new IllegalArgumentException("Talep edilen gün sayısı 0'dan büyük olmalı");
                    }

                    int requestYear = request.getDate() != null ? request.getDate() : LocalDate.now().getYear();

                    List<LeaveBalance> balances = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(employee.getId(), leaveType.getId());

                    // Policy service ile earned days hesaplama
                    LeavePolicyRequest policyReq = LeavePolicyRequest.builder()
                            .employeeId(employee.getId())
                            .leaveTypeId(leaveType.getId())
                            .build();

                    int earnedDays;
                    if ("annual leave".equalsIgnoreCase(leaveName) || "yıllık izin".equalsIgnoreCase(leaveName)) {
                        // Sync call - reactive context'te uygun değil ama mevcut yapı korundu
                        earnedDays = leavePolicyService.getMaxAllowedDaysForEmployeeAndType(policyReq).block();
                    } else if (leaveType.getDefaultDays() != null) {
                        earnedDays = leaveType.getDefaultDays();
                    } else {
                        earnedDays = Integer.MAX_VALUE;
                    }

                    // Yıllık kullanılan gün kontrolü
                    int usedInYear = balances.stream()
                            .filter(b -> b.getYear().equals(requestYear))
                            .mapToInt(LeaveBalance::getUsedDays)
                            .sum();

                    if (earnedDays != Integer.MAX_VALUE && usedInYear + requestedDays > earnedDays) {
                        throw new IllegalArgumentException(
                                leaveType.getName() + " için izin talebi maksimum gün sayısını aşıyor (" + earnedDays + ")"
                        );
                    }

                    // Önceki yıldan devredilen gün hesaplama
                    int carriedOver = balances.stream()
                            .filter(b -> b.getYear().equals(requestYear - 1))
                            .findFirst()
                            .map(b -> Math.max(0, b.getEarnedDays() - b.getUsedDays()))
                            .orElse(0);

                    // Mevcut yıl balance'ı kontrol et
                    Optional<LeaveBalance> existingYearBalance = balances.stream()
                            .filter(b -> b.getYear().equals(requestYear))
                            .findFirst();

                    LeaveBalance savedBalance;
                    if (existingYearBalance.isPresent()) {
                        // Mevcut balance'ı güncelle
                        LeaveBalance existing = existingYearBalance.get();
                        int newEarnedAmount = earnedDays == Integer.MAX_VALUE
                                ? existing.getEarnedDays() + requestedDays
                                : earnedDays + carriedOver;

                        existing.setEarnedDays(newEarnedAmount);
                        existing.setUsedDays(existing.getUsedDays() + requestedDays);
                        existing.setCarriedOverDays(carriedOver);

                        savedBalance = leaveBalanceRepository.save(existing);
                    } else {
                        // Yeni balance oluştur
                        int totalAmount = (earnedDays == Integer.MAX_VALUE) ? requestedDays : (earnedDays + carriedOver);

                        LeaveBalance newBalance = LeaveBalance.builder()
                                .employee(employee)
                                .leaveType(leaveType)
                                .year(requestYear)
                                .earnedDays(totalAmount)
                                .usedDays(requestedDays)
                                .carriedOverDays(carriedOver)
                                .build();

                        savedBalance = leaveBalanceRepository.save(newBalance);
                    }

                    // Güncel balance listesini al
                    List<LeaveBalance> allBalances = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(employee.getId(), leaveType.getId());

                    return leaveBalanceMapper.toResponse(savedBalance, allBalances);
                })
                .subscribeOn(Schedulers.boundedElastic());
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

                    List<LeaveBalance> allBalances = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(updated.getEmployee().getId(), updated.getLeaveType().getId());

                    Logger.logUpdated(LeaveBalance.class, updated.getId(), MESSAGE);
                    return leaveBalanceMapper.toResponse(updated, allBalances);
                })
                .subscribeOn(Schedulers.boundedElastic());
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
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployee(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId cannot be null"));
        }

        return Mono.fromCallable(() -> {
                    List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeId(request.getId());
                    return leaveBalanceMapper.toResponseList(balances);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and Year are required"));
        }

        return Mono.fromCallable(() -> {
                    List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeIdAndYear(request.getEmployeeId(), request.getYear());
                    return leaveBalanceMapper.toResponseList(balances);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and LeaveTypeId are required"));
        }

        return Mono.fromCallable(() -> {
                    LeaveBalance lb = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                            .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                    "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId()));

                    List<LeaveBalance> allBalances = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(lb.getEmployee().getId(), lb.getLeaveType().getId());

                    return leaveBalanceMapper.toResponse(lb, allBalances);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId and Year are required"));
        }

        return Mono.fromCallable(() -> {
                    LeaveBalance lb = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                                    request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())
                            .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                    "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId() + ", Year: " + request.getYear()));

                    List<LeaveBalance> allBalances = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(lb.getEmployee().getId(), lb.getLeaveType().getId());

                    return leaveBalanceMapper.toResponse(lb, allBalances);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        if (request == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("LeaveTypeId and Year are required"));
        }

        return Mono.fromCallable(() -> {
                    List<LeaveBalance> balances = leaveBalanceRepository.findByLeaveTypeIdAndYear(request.getLeaveTypeId(), request.getYear());
                    return leaveBalanceMapper.toResponseList(balances);
                })
                .subscribeOn(Schedulers.boundedElastic());
    }
}