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
import com.neg.technology.human.resource.leave.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.request.LeaveTypeYearRequest;
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
        // Null gelirse default tarih ata
        if (request.getEffectiveDate() == null) {
            request.setEffectiveDate(LocalDate.of(LocalDate.now().getYear(), 1, 1));
        }

        return Mono.zip(
                Mono.fromCallable(() -> employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()))),
                Mono.fromCallable(() -> leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId())))
        ).flatMap(tuple -> {
            Employee employee = tuple.getT1();
            LeaveType leaveType = tuple.getT2();

            leaveBalanceValidator.validateLeaveCreation(request.getAmount(), employee, leaveType);

            // Repository metodunu ve DTO'daki alan adını tutarlı hale getirin.
            return Mono.fromCallable(() -> leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndEffectiveDate(
                    request.getEmployeeId(), request.getLeaveTypeId(), request.getEffectiveDate()
            )).flatMap(existingBalanceOpt -> {
                if (existingBalanceOpt.isPresent()) {
                    return Mono.error(new IllegalArgumentException("Leave balance for this employee, leave type and year already exists. Use update method instead."));
                }
                // LeaveBalance.builder() çağrısını DTO ile uyumlu hale getirin.
                LeaveBalance entity = leaveBalanceMapper.toEntity(request, employee, leaveType);
                LeaveBalance saved = leaveBalanceRepository.save(entity);
                Logger.logCreated(LeaveBalance.class, saved.getId(), "LeaveBalance");
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

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(leaveBalanceRepository.findByEmployeeId(request.getId()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and Year are required"));
        }

        // Repository metot adını DTO ve varlık ile uyumlu hale getirin.
        // DTO'da year, varlıkta effectiveDate var. Yıl aralığı ile arama yapmalısınız.
        LocalDate startDate = LocalDate.of(request.getYear(), 1, 1);
        LocalDate endDate = LocalDate.of(request.getYear(), 12, 31);

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByEmployeeIdAndEffectiveDateBetween(
                                request.getEmployeeId(),
                                startDate,
                                endDate
                        )
                )
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and LeaveTypeId are required"));
        }

        return Mono.fromCallable(() ->
                        leaveBalanceRepository
                                .findByEmployeeIdAndLeaveTypeIdOrderByEffectiveDateAsc(request.getEmployeeId(), request.getLeaveTypeId())
                                .stream().findFirst()
                                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                        "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId()))
                )
                .map(leaveBalanceMapper::toResponse)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId and Year are required"));
        }

        LocalDate startDate = LocalDate.of(request.getYear(), 1, 1);
        LocalDate endDate = LocalDate.of(request.getYear(), 12, 31);

        return Mono.fromCallable(() -> {
            List<LeaveBalance> balances = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndEffectiveDateBetween(
                            request.getEmployeeId(), request.getLeaveTypeId(), startDate, endDate
                    );

            if (balances.isEmpty()) {
                throw new ResourceNotFoundException(MESSAGE,
                        "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId() + ", Year: " + request.getYear());
            }

            return leaveBalanceMapper.toResponse(balances.get(0));
        }).subscribeOn(Schedulers.boundedElastic());
    }



    @Override
    public Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        if (request == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("LeaveTypeId and Year are required"));
        }

        // Repository metot adını DTO ve varlık ile uyumlu hale getirin.
        LocalDate startDate = LocalDate.of(request.getYear(), 1, 1);
        LocalDate endDate = LocalDate.of(request.getYear(), 12, 31);

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByLeaveTypeIdAndEffectiveDateBetween(request.getLeaveTypeId(), startDate, endDate)
                )
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> deductLeave(DeductLeaveRequest request) {
        LocalDate startDate = LocalDate.of(request.getYear(), 1, 1);
        LocalDate endDate = LocalDate.of(request.getYear(), 12, 31);

        // Reactive olarak repository çağrısı
        return Mono.fromCallable(() ->
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndEffectiveDateBetween(
                        request.getEmployeeId(),
                        request.getLeaveTypeId(),
                        startDate,
                        endDate
                )
        ).flatMap(balances -> {
            if (balances.isEmpty()) {
                return Mono.error(new ResourceNotFoundException("Leave Balance", -1L));
            }

            BigDecimal totalBalance = leaveBalanceValidator.calculateTotalBalance(balances);
            leaveBalanceValidator.hasEnoughBalance(totalBalance, request.getAmount());

            BigDecimal remainingToDeduct = request.getAmount();
            for (LeaveBalance balance : balances) {
                BigDecimal available = balance.getAmount().subtract(BigDecimal.valueOf(balance.getUsedDays()));
                if (available.compareTo(remainingToDeduct) >= 0) {
                    balance.setUsedDays(balance.getUsedDays() + remainingToDeduct.intValue());
                    remainingToDeduct = BigDecimal.ZERO;
                    break;
                } else {
                    balance.setUsedDays(balance.getUsedDays() + available.intValue());
                    remainingToDeduct = remainingToDeduct.subtract(available);
                }
            }

            return Mono.fromCallable(() -> leaveBalanceRepository.saveAll(balances))
                    .subscribeOn(Schedulers.boundedElastic())
                    .then();
        }).subscribeOn(Schedulers.boundedElastic());
    }


    @Override
    public Mono<Void> addLeave(AddLeaveRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId and Year are required"));
        }

        LocalDate startDate = LocalDate.of(request.getYear(), 1, 1);
        LocalDate endDate = LocalDate.of(request.getYear(), 12, 31);

        return Mono.zip(
                Mono.fromCallable(() -> employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()))),
                Mono.fromCallable(() -> leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId())))
        ).flatMap(tuple -> {
            Employee employee = tuple.getT1();
            LeaveType leaveType = tuple.getT2();

            // Yeni yıl balance'ini kontrol et
            List<LeaveBalance> currentYearBalances = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndEffectiveDateBetween(
                            request.getEmployeeId(), request.getLeaveTypeId(), startDate, endDate
                    );

            LeaveBalance currentYearBalance;
            if (!currentYearBalances.isEmpty()) {
                currentYearBalance = currentYearBalances.get(0);
            } else {
                // Eğer yoksa yeni balance oluştur
                currentYearBalance = LeaveBalance.builder()
                        .employee(employee)
                        .leaveType(leaveType)
                        .amount(BigDecimal.ZERO)
                        .effectiveDate(startDate)
                        .usedDays(0)
                        .build();
            }

            // Geçmiş yıllardan kalan izinleri ekle
            List<LeaveBalance> pastBalances = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdOrderByEffectiveDateAsc(request.getEmployeeId(), request.getLeaveTypeId());

            BigDecimal carryOver = pastBalances.stream()
                    .filter(b -> b.getEffectiveDate().isBefore(startDate))
                    .map(b -> b.getAmount().subtract(BigDecimal.valueOf(b.getUsedDays())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            currentYearBalance.setAmount(currentYearBalance.getAmount().add(request.getAmount()).add(carryOver));

            leaveBalanceRepository.save(currentYearBalance);

            return Mono.empty();
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }


}