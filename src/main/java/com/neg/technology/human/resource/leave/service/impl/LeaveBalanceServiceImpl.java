package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
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

import java.math.BigDecimal;

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
        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(leaveBalanceRepository.findAll())
        );
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
        );
    }

    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and LeaveTypeId are required"));
        }

        Mono<Employee> employeeMono = Mono.fromCallable(() ->
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()))
        );

        Mono<LeaveType> leaveTypeMono = Mono.fromCallable(() ->
                leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()))
        );

        return Mono.zip(employeeMono, leaveTypeMono)
                .flatMap(tuple -> {
                    Employee employee = tuple.getT1();
                    LeaveType leaveType = tuple.getT2();

                    String gender = employee.getPerson() != null ? employee.getPerson().getGender() : null;
                    int requestedDays = request.getAmount() != null ? request.getAmount().intValue() : 0;

                    // Sadece "Maternity Leave" üzerinden akış
                    if (!"maternity leave".equalsIgnoreCase(leaveType.getName())) {
                        // İstersen burada farklı izinleri de policy'den yönetebilirsin.
                        // Şimdilik güvenli olması için engelliyoruz:
                        return Mono.error(new RuntimeException("Only 'Maternity Leave' is supported for now"));
                    }

                    if (gender == null) {
                        return Mono.error(new RuntimeException("Employee gender is unknown"));
                    }

                    // KADIN: max gün policy’den
                    if ("female".equalsIgnoreCase(gender)) {
                        return leavePolicyService.getMaternityLeaveDays(
                                        LeavePolicyRequest.builder()
                                                .employeeId(employee.getId())
                                                // .date(LocalDate.of(request.getDate(), 1, 1)) // GEREKİRSE böyle ver
                                                .multiplePregnancy(request.getMultiplePregnancy()) // varsa
                                                .build()
                                )
                                .flatMap(policy -> {
                                    Integer maxDays = policy.getDays(); // 112 / 140 vb.
                                    if (maxDays == null) {
                                        return Mono.error(new RuntimeException("Policy did not return max days"));
                                    }
                                    if (requestedDays > maxDays) {
                                        return Mono.error(new RuntimeException(
                                                "Requested leave exceeds maximum allowed days (" + maxDays + ")"
                                        ));
                                    }
                                    // kayıt
                                    LeaveBalance entity = LeaveBalance.builder()
                                            .employee(employee)
                                            .leaveType(leaveType)
                                            .date(request.getDate()) // request.getDate() muhtemelen Integer (yıl)
                                            .amount(BigDecimal.valueOf(requestedDays))
                                            .usedDays(0)
                                            .build();

                                    LeaveBalance saved = leaveBalanceRepository.save(entity);
                                    Logger.logCreated(LeaveBalance.class, saved.getId(), MESSAGE);

                                    LeaveBalanceResponse response = LeaveBalanceResponse.builder()
                                            .id(saved.getId())
                                            .employeeFirstName(saved.getEmployee().getPerson().getFirstName())
                                            .employeeLastName(saved.getEmployee().getPerson().getLastName())
                                            .leaveTypeName(saved.getLeaveType().getName())
                                            .leaveTypeBorrowableLimit(saved.getLeaveType().getBorrowableLimit())
                                            .leaveTypeIsUnpaid(saved.getLeaveType().getIsUnpaid())
                                            .date(saved.getDate())
                                            .amount(saved.getAmount())
                                            .build();

                                    return Mono.just(response);
                                });
                    }

                    // ERKEK: max 5 gün (policy’e bağlamak istemezsen sabit)
                    if ("male".equalsIgnoreCase(gender)) {
                        int maxDays = 5;
                        if (requestedDays > maxDays) {
                            return Mono.error(new RuntimeException(
                                    "Requested leave exceeds maximum allowed days (" + maxDays + ")"
                            ));
                        }

                        LeaveBalance entity = LeaveBalance.builder()
                                .employee(employee)
                                .leaveType(leaveType)
                                .date(request.getDate()) // Integer yıl
                                .amount(BigDecimal.valueOf(requestedDays))
                                .usedDays(0)
                                .build();

                        LeaveBalance saved = leaveBalanceRepository.save(entity);
                        Logger.logCreated(LeaveBalance.class, saved.getId(), MESSAGE);

                        LeaveBalanceResponse response = LeaveBalanceResponse.builder()
                                .id(saved.getId())
                                .employeeFirstName(saved.getEmployee().getPerson().getFirstName())
                                .employeeLastName(saved.getEmployee().getPerson().getLastName())
                                .leaveTypeName(saved.getLeaveType().getName())
                                .leaveTypeBorrowableLimit(saved.getLeaveType().getBorrowableLimit())
                                .leaveTypeIsUnpaid(saved.getLeaveType().getIsUnpaid())
                                .date(saved.getDate())
                                .amount(saved.getAmount())
                                .build();

                        return Mono.just(response);
                    }

                    // Diğer durumlar (non-binary vs.) – şimdilik engelle
                    return Mono.error(new RuntimeException("Unsupported gender for Maternity Leave"));
                });
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
        });
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
        });
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployee(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId cannot be null"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByEmployeeId(request.getId())
                )
        );
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and Year are required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByEmployeeIdAndDate(request.getYear(), request.getEmployeeId())
                )
        );
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId and LeaveTypeId are required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId and Year are required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndDate(
                                request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId() + ", Year: " + request.getYear()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        if (request == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("LeaveTypeId and Year are required"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByLeaveTypeIdAndDate(request.getLeaveTypeId(), request.getYear())
                )
        );
    }
}
