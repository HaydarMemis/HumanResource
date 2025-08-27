package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveRequest;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.mapper.LeaveRequestMapper;
import com.neg.technology.human.resource.leave.model.request.ChangeLeaveRequestStatusRequest;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.response.*;
import com.neg.technology.human.resource.leave.repository.LeaveRequestRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveRequestService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.StatusRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    public Mono<LeaveRequestResponseList> getAll() {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> entities = leaveRequestRepository.findAll();
            List<LeaveRequestResponse> responses = entities.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponse> getById(IdRequest request) {
        return Mono.fromCallable(() ->
                leaveRequestRepository.findById(request.getId())
                        .map(LeaveRequestMapper::toDTO)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave Request", request.getId()))
        );
    }

    @Override
    public Mono<LeaveRequestResponse> create(CreateLeaveRequestRequest dto) {
        return Mono.fromCallable(() -> {
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", dto.getEmployeeId()));

            LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave Type", dto.getLeaveTypeId()));

            Employee approver = null;
            if(dto.getApprovedById() != null){
                approver = employeeRepository.findById(dto.getApprovedById())
                        .orElseThrow(() -> new ResourceNotFoundException("Approver Employee", dto.getApprovedById()));
            }

            LeaveRequest entity = LeaveRequestMapper.toEntity(dto, employee, leaveType, approver);
            LeaveRequest saved = leaveRequestRepository.save(entity);

            Logger.logCreated(LeaveRequest.class, saved.getId(), "LeaveRequest");

            return LeaveRequestMapper.toDTO(saved);
        });
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
        });
    }

    @Override
    public Mono<Void> delete(IdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!leaveRequestRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException("Leave Request", request.getId());
            }
            leaveRequestRepository.deleteById(request.getId());
            Logger.logDeleted(LeaveRequest.class, request.getId());
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByEmployee(IdRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeId(request.getId());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByStatus(StatusRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByStatus(request.getStatus());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getCancelled() {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByIsCancelledTrue();
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByApprover(IdRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByApprovedById(request.getId());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByEmployeeAndStatus(EmployeeStatusRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndStatus(request.getEmployeeId(), request.getStatus());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByDateRange(EmployeeDateRangeRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByStartDateBetween(request.getStartDate(), request.getEndDate());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
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
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
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
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<ApprovedLeaveDatesResponseList> getApprovedByEmployee(IdRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndStatus(request.getId(), "APPROVED");
            List<ApprovedLeaveDatesResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toApprovedDatesDTO)
                    .toList();
            return new ApprovedLeaveDatesResponseList(responses);
        });
    }

    @Override
    public Mono<ChangeLeaveRequestStatusResponseList> changeStatus(ChangeLeaveRequestStatusRequest request) {
        return Mono.fromCallable(() -> {
            // blocking JPA call
            return leaveRequestRepository.findById(request.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Leave request not found with id " + request.getId()));
        }).map(entity -> {
            String oldStatus = entity.getStatus();

            entity.setStatus(request.getStatus());

            LeaveRequest saved = leaveRequestRepository.save(entity);

            ChangeLeaveRequestStatusResponse response = LeaveRequestMapper.toChangeStatusDTO(saved, oldStatus);

            // Wrap inside list
            return ChangeLeaveRequestStatusResponseList.builder()
                    .responses(List.of(response))
                    .build();
        });
    }
}