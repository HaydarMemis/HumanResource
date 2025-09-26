package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.mapper.LeaveBalanceMapper;
import com.neg.technology.human.resource.leave.model.request.*;
import com.neg.technology.human.resource.leave.model.response.*;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.leave.validator.LeaveBalanceValidator;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.exception.LeaveBalanceExceededException;
import com.neg.technology.human.resource.exception.InvalidLeaveRequestException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
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

    // -------------------- READ OPERATIONS --------------------

    @Override
    public Mono<LeaveBalanceResponseList> getAll() {
        return Mono.fromCallable((Callable<LeaveBalanceResponseList>) () -> {
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
        return Mono.fromCallable((Callable<LeaveBalanceResponse>) () ->
                leaveBalanceRepository.findById(request.getId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployee(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId cannot be null"));
        }
        return Mono.fromCallable((Callable<LeaveBalanceResponseList>) () ->
                leaveBalanceMapper.toResponseList(leaveBalanceRepository.findByEmployeeId(request.getId()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and Year are required"));
        }
        return Mono.fromCallable((Callable<LeaveBalanceResponseList>) () ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByEmployeeIdAndYear(request.getEmployeeId(), request.getYear()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and LeaveTypeId are required"));
        }
        return Mono.fromCallable((Callable<LeaveBalanceResponse>) () -> {
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdOrderByYearAsc(request.getEmployeeId(), request.getLeaveTypeId())
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                            "Employee: " + request.getEmployeeId() +
                                    ", LeaveType: " + request.getLeaveTypeId()));
            return leaveBalanceMapper.toResponse(balance);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null
                || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId and Year are required"));
        }
        return Mono.fromCallable((Callable<LeaveBalanceResponse>) () -> {
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(request.getEmployeeId(),
                            request.getLeaveTypeId(),
                            request.getYear())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                            "Employee: " + request.getEmployeeId() +
                                    ", LeaveType: " + request.getLeaveTypeId() +
                                    ", Year: " + request.getYear()));
            return leaveBalanceMapper.toResponse(balance);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        if (request == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("LeaveTypeId and Year are required"));
        }
        return Mono.fromCallable((Callable<LeaveBalanceResponseList>) () ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByLeaveTypeIdAndYear(request.getLeaveTypeId(), request.getYear()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    // -------------------- CREATE --------------------

    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Request cannot be null"));
        }

        return Mono.fromCallable((Callable<LeaveBalanceResponse>) () -> {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

            LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

            leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(
                            request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())
                    .ifPresent(b -> {
                        throw new IllegalArgumentException("Leave balance already exists. Use update instead.");
                    });

            LeaveBalance entity = leaveBalanceMapper.toEntity(request, employee, leaveType);

            if (entity.getAdvanceDays() == null) {
                entity.setAdvanceDays(BigDecimal.valueOf(5));
            }
            // Validate creation against policy / annual allowance
            leaveBalanceValidator.validateLeaveCreation(Optional.ofNullable(entity.getTotalDays()).orElse(BigDecimal.ZERO), employee, leaveType);

            LeaveBalance saved = leaveBalanceRepository.save(entity);

            Logger.logCreated(LeaveBalance.class, saved.getId(), MESSAGE);
            return leaveBalanceMapper.toResponse(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // -------------------- UPDATE --------------------

    @Override
    public Mono<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id is required for update"));
        }

        return Mono.fromCallable((Callable<LeaveBalanceResponse>) () -> {
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
            // After mapping, validate that totalDays does not exceed allowed
            ensureTotalWithinAllowance(existing);

            LeaveBalance updated = leaveBalanceRepository.save(existing);

            Logger.logUpdated(LeaveBalance.class, updated.getId(), MESSAGE);
            return leaveBalanceMapper.toResponse(updated);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // -------------------- DELETE --------------------

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

    // -------------------- LEAVE OPERATIONS --------------------

    @Override
    public Mono<Void> deductLeave(DeductLeaveRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null
                || request.getYear() == null || request.getAmount() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId, Year and Amount are required"));
        }

        return Mono.fromRunnable(() -> {
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                            "Employee: " + request.getEmployeeId() +
                                    ", LeaveType: " + request.getLeaveTypeId() +
                                    ", Year: " + request.getYear()));

            BigDecimal available = Optional.ofNullable(balance.getAvailableBalance()).orElse(BigDecimal.ZERO);
            leaveBalanceValidator.hasEnoughBalance(available, request.getAmount());
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

        // Blocking DB ops wrapped in boundedElastic
        return Mono.fromCallable(() -> {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));

            LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())
                    .orElseGet(() -> {
                        LeaveBalance lb = LeaveBalance.builder()
                                .employee(employee)
                                .leaveType(leaveType)
                                .year(request.getYear())
                                .totalDays(BigDecimal.ZERO)
                                .usedDays(BigDecimal.ZERO)
                                .advanceDays(BigDecimal.valueOf(5))
                                .build();
                        return lb;
                    });

            BigDecimal carryOver = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                    .stream()
                    .filter(b -> b.getYear() < request.getYear())
                    .map(b -> Optional.ofNullable(b.getAvailableBalance()).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal currentTotal = Optional.ofNullable(balance.getTotalDays()).orElse(BigDecimal.ZERO);
            BigDecimal amountToAdd = request.getAmount();
            BigDecimal effectiveNewTotal = currentTotal.add(amountToAdd).add(Optional.ofNullable(carryOver).orElse(BigDecimal.ZERO));

            // Validate rules (gender, limits) using the validator helper
            // getAnnualLeaveAllowance checks gender for maternity/paternity and returns allowance for those types
            // validateLeaveCreation checks total against allowance when allowance > 0
            leaveBalanceValidator.getAnnualLeaveAllowance(employee, leaveType); // may throw InvalidLeaveRequestException for gender mismatch
            leaveBalanceValidator.validateLeaveCreation(effectiveNewTotal, employee, leaveType);

            // save new total
            balance.setTotalDays(effectiveNewTotal);
            leaveBalanceRepository.save(balance);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    // -------------------- PRIVATE HELPERS --------------------

    private void ensureTotalWithinAllowance(LeaveBalance balance) {
        if (balance == null) return;
        BigDecimal total = Optional.ofNullable(balance.getTotalDays()).orElse(BigDecimal.ZERO);
        Employee employee = balance.getEmployee();
        LeaveType leaveType = balance.getLeaveType();

        if (employee == null || leaveType == null) return;

        BigDecimal allowance = leaveBalanceValidator.getAnnualLeaveAllowance(employee, leaveType);
        if (allowance.compareTo(BigDecimal.ZERO) > 0 && total.compareTo(allowance) > 0) {
            throw new LeaveBalanceExceededException("Güncellenen izin toplamı yıllık limiti aşıyor. Limit: " + allowance + ", Güncel: " + total);
        }
    }
}
