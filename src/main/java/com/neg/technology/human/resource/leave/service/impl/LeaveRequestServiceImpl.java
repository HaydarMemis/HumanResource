package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveRequest;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.enums.LeaveStatus;
import com.neg.technology.human.resource.leave.model.mapper.LeaveRequestMapper;
import com.neg.technology.human.resource.leave.model.request.AddLeaveRequest;
import com.neg.technology.human.resource.leave.model.request.ChangeLeaveRequestStatusRequest;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.DeductLeaveRequest;
import com.neg.technology.human.resource.leave.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveRequestRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.leave.service.LeaveRequestService;
import com.neg.technology.human.resource.leave.validator.LeaveRequestValidator;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.StatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestValidator leaveRequestValidator;
    private final LeaveBalanceService leaveBalanceService;

    private Mono<Employee> getEmployee(Long employeeId) {
        return Mono.fromCallable(() ->
                employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", employeeId))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<LeaveType> getLeaveType(Long leaveTypeId) {
        return Mono.fromCallable(() ->
                leaveTypeRepository.findById(leaveTypeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave Type", leaveTypeId))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    /* ---------- helper: string -> enum parse (throws if invalid) ---------- */
    private LeaveStatus parseStatus(String status) {
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        try {
            return LeaveStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    /* -------------------- normal CRUD + queries -------------------- */

    @Override
    public Mono<LeaveRequestResponseList> getAll() {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> entities = leaveRequestRepository.findAll();
            List<LeaveRequestResponse> responses = entities.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponse> getById(IdRequest request) {
        return Mono.fromCallable(() ->
                leaveRequestRepository.findById(request.getId())
                        .map(LeaveRequestMapper::toDTO)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave Request", request.getId()))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponse> create(CreateLeaveRequestRequest dto) {
        return Mono.zip(
                getEmployee(dto.getEmployeeId()),
                getLeaveType(dto.getLeaveTypeId()),
                dto.getApprovedById() != null ? getEmployee(dto.getApprovedById()) : Mono.just(null)
        ).flatMap(tuple -> {
            Employee employee = tuple.getT1();
            LeaveType leaveType = tuple.getT2();
            Employee approver = tuple.getT3();

            BigDecimal requestedDays = BigDecimal.valueOf(ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1);

            return leaveRequestValidator.validateLeaveRequestCreation(employee, leaveType, dto.getStartDate(), dto.getEndDate(), requestedDays)
                    .flatMap(ignored -> leaveBalanceService.deductLeave(new DeductLeaveRequest(employee.getId(), leaveType.getId(), requestedDays, dto.getStartDate().getYear())))
                    .then(Mono.fromCallable(() -> {
                        LeaveRequest entity = LeaveRequestMapper.toEntity(dto, employee, leaveType, approver);
                        entity.setStatus(LeaveStatus.PENDING);
                        LeaveRequest saved = leaveRequestRepository.save(entity);
                        Logger.logCreated(LeaveRequest.class, saved.getId(), "LeaveRequest");
                        return LeaveRequestMapper.toDTO(saved);
                    }));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponse> update(UpdateLeaveRequestRequest dto) {
        return Mono.fromCallable(() -> {
            LeaveRequest existing = leaveRequestRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave Request", dto.getId()));

            Employee employee = null;
            if (dto.getEmployeeId() != null) {
                employee = employeeRepository.findById(dto.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", dto.getEmployeeId()));
            }

            LeaveType leaveType = null;
            if (dto.getLeaveTypeId() != null) {
                leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Leave Type", dto.getLeaveTypeId()));
            }

            Employee approver = null;
            if (dto.getApprovedById() != null) {
                approver = employeeRepository.findById(dto.getApprovedById())
                        .orElseThrow(() -> new ResourceNotFoundException("Approver Employee", dto.getApprovedById()));
            }

            LeaveRequestMapper.updateEntity(existing, dto, employee, leaveType, approver);

            LeaveRequest updated = leaveRequestRepository.save(existing);

            Logger.logUpdated(LeaveRequest.class, updated.getId(), "LeaveRequest");

            return LeaveRequestMapper.toDTO(updated);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Void> delete(IdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!leaveRequestRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException("Leave Request", request.getId());
            }
            leaveRequestRepository.deleteById(request.getId());
            Logger.logDeleted(LeaveRequest.class, request.getId());
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    public Mono<LeaveRequestResponseList> getByEmployee(IdRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeId(request.getId());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /* ---------- FIXED: status methods must use LeaveStatus enum ---------- */

    @Override
    public Mono<LeaveRequestResponseList> getByStatus(StatusRequest request) {
        return Mono.fromCallable(() -> {
            LeaveStatus status = parseStatus(request.getStatus());
            List<LeaveRequest> list = leaveRequestRepository.findByStatus(status);
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponseList> getCancelled() {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByIsCancelledTrue();
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponseList> getByApprover(IdRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByApprovedById(request.getId());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponseList> getByEmployeeAndStatus(EmployeeStatusRequest request) {
        return Mono.fromCallable(() -> {
            LeaveStatus status = parseStatus(request.getStatus());
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndStatus(request.getEmployeeId(), status);
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponseList> getByDateRange(EmployeeDateRangeRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByStartDateBetween(
                    request.getStartDate(),
                    request.getEndDate()
            );
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponseList> getByEmployeeLeaveTypeAndDateRange(EmployeeLeaveTypeDateRangeRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(
                    request.getEmployeeId(),
                    request.getLeaveTypeId(),
                    request.getStartDate(),
                    request.getEndDate()
            );
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponseList> getOverlapping(EmployeeDateRangeRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findOverlappingRequests(
                    request.getEmployeeId(),
                    request.getStartDate(),
                    request.getEndDate()
            );
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponseList> getApprovedByEmployee(Long employeeId) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, LeaveStatus.APPROVED);
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .collect(Collectors.toList());
            return new LeaveRequestResponseList(responses);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<LeaveRequestResponse> changeStatus(ChangeLeaveRequestStatusRequest dto) {
        return Mono.fromCallable(() ->
                leaveRequestRepository.findById(dto.getLeaveRequestId())
                        .orElseThrow(() -> new ResourceNotFoundException("Leave Request", dto.getLeaveRequestId()))
        ).flatMap(existing -> {
            LeaveStatus oldStatus = existing.getStatus();
            LeaveStatus newStatus = dto.getStatus(); // dto.status already enum (as you changed it)

            BigDecimal requestedDays = BigDecimal.valueOf(ChronoUnit.DAYS.between(existing.getStartDate(), existing.getEndDate()) + 1);

            return leaveRequestValidator.validateStatusChange(existing, dto)
                    .then(Mono.defer(() -> {
                        if (LeaveStatus.APPROVED.equals(newStatus) && !LeaveStatus.APPROVED.equals(oldStatus)) {
                            return leaveBalanceService.getByEmployeeAndLeaveType(
                                            new EmployeeLeaveTypeRequest(existing.getEmployee().getId(), existing.getLeaveType().getId()))
                                    .flatMap(balance -> {
                                        if (balance.getTotalDays().compareTo(requestedDays) < 0) {
                                            return Mono.error(new RuntimeException("Insufficient leave balance."));
                                        }
                                        return leaveBalanceService.deductLeave(new DeductLeaveRequest(
                                                        existing.getEmployee().getId(),
                                                        existing.getLeaveType().getId(),
                                                        requestedDays,
                                                        existing.getStartDate().getYear()))
                                                .then(updateLeaveRequest(existing, newStatus, dto.getApprovalNote()));
                                    });
                        } else if ((LeaveStatus.REJECTED.equals(newStatus) || LeaveStatus.CANCELLED.equals(newStatus))
                                && LeaveStatus.APPROVED.equals(oldStatus)) {
                            return leaveBalanceService.addLeave(new AddLeaveRequest(
                                            existing.getEmployee().getId(),
                                            existing.getLeaveType().getId(),
                                            requestedDays,
                                            existing.getStartDate().getYear()))
                                    .then(updateLeaveRequest(existing, newStatus, dto.getApprovalNote()));
                        } else {
                            return updateLeaveRequest(existing, newStatus, dto.getApprovalNote());
                        }
                    }))
                    .map(LeaveRequestMapper::toDTO);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<LeaveRequest> updateLeaveRequest(LeaveRequest existing, LeaveStatus newStatus, String approvalNote) {
        existing.setStatus(newStatus);
        if (approvalNote != null) existing.setApprovalNote(approvalNote);
        return Mono.fromCallable(() -> leaveRequestRepository.save(existing))
                .subscribeOn(Schedulers.boundedElastic());
    }

}
