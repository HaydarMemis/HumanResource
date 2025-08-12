package com.neg.technology.human.resource.LeaveRequest.service;

import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.LeaveRequest.model.entity.LeaveRequest;
import com.neg.technology.human.resource.LeaveRequest.model.mapper.LeaveRequestMapper;
import com.neg.technology.human.resource.LeaveRequest.model.request.*;
import com.neg.technology.human.resource.LeaveRequest.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.LeaveRequest.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.LeaveRequest.repository.LeaveRequestRepository;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.LeaveType.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.StatusRequest;
import com.neg.technology.human.resource.Employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.Employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.LeaveType.model.request.EmployeeLeaveTypeDateRangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    public LeaveRequestResponseList getAll() {
        List<LeaveRequest> entities = leaveRequestRepository.findAll();
        List<LeaveRequestResponse> responses = entities.stream()
                .map(LeaveRequestMapper::toDTO)
                .collect(Collectors.toList());
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponse getById(IdRequest request) {
        LeaveRequest entity = leaveRequestRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Request", request.getId()));
        return LeaveRequestMapper.toDTO(entity);
    }

    @Override
    public LeaveRequestResponse create(CreateLeaveRequestRequest dto) {
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

        BusinessLogger.logCreated(LeaveRequest.class, saved.getId(), "LeaveRequest");

        return LeaveRequestMapper.toDTO(saved);
    }

    @Override
    public LeaveRequestResponse update(UpdateLeaveRequestRequest dto) {
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

        BusinessLogger.logUpdated(LeaveRequest.class, updated.getId(), "LeaveRequest");

        return LeaveRequestMapper.toDTO(updated);
    }

    @Override
    public void delete(IdRequest request) {
        if (!leaveRequestRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("Leave Request", request.getId());
        }
        leaveRequestRepository.deleteById(request.getId());
        BusinessLogger.logDeleted(LeaveRequest.class, request.getId());
    }

    @Override
    public LeaveRequestResponseList getByEmployee(IdRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByEmployeeId(request.getId());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .collect(Collectors.toList());
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByStatus(StatusRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByStatus(request.getStatus());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .collect(Collectors.toList());
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getCancelled() {
        List<LeaveRequest> list = leaveRequestRepository.findByIsCancelledTrue();
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .collect(Collectors.toList());
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByApprover(IdRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByApprovedById(request.getId());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .collect(Collectors.toList());
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByEmployeeAndStatus(EmployeeStatusRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndStatus(request.getEmployeeId(), request.getStatus());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .collect(Collectors.toList());
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByDateRange(EmployeeDateRangeRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByStartDateBetween(request.getStartDate(), request.getEndDate());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .collect(Collectors.toList());
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByEmployeeLeaveTypeAndDateRange(EmployeeLeaveTypeDateRangeRequest request) {
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
    }

    @Override
    public LeaveRequestResponseList getOverlapping(EmployeeDateRangeRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findOverlappingRequests(
                request.getEmployeeId(),
                request.getStartDate(),
                request.getEndDate()
        );
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .collect(Collectors.toList());
        return new LeaveRequestResponseList(responses);
    }
}
