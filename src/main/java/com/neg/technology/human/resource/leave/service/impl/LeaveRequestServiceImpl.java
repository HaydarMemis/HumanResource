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
import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveRequestRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveRequestService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.StatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
                .toList();
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

        Logger.logCreated(LeaveRequest.class, saved.getId(), "LeaveRequest");

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

        Logger.logUpdated(LeaveRequest.class, updated.getId(), "LeaveRequest");

        return LeaveRequestMapper.toDTO(updated);
    }

    @Override
    public void delete(IdRequest request) {
        if (!leaveRequestRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("Leave Request", request.getId());
        }
        leaveRequestRepository.deleteById(request.getId());
        Logger.logDeleted(LeaveRequest.class, request.getId());
    }

    @Override
    public LeaveRequestResponseList getByEmployee(IdRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByEmployeeId(request.getId());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByStatus(StatusRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByStatus(request.getStatus());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getCancelled() {
        List<LeaveRequest> list = leaveRequestRepository.findByIsCancelledTrue();
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByApprover(IdRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByApprovedById(request.getId());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByEmployeeAndStatus(EmployeeStatusRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndStatus(request.getEmployeeId(), request.getStatus());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
        return new LeaveRequestResponseList(responses);
    }

    @Override
    public LeaveRequestResponseList getByDateRange(EmployeeDateRangeRequest request) {
        List<LeaveRequest> list = leaveRequestRepository.findByStartDateBetween(request.getStartDate(), request.getEndDate());
        List<LeaveRequestResponse> responses = list.stream()
                .map(LeaveRequestMapper::toDTO)
                .toList();
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
                .toList();
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
                .toList();
        return new LeaveRequestResponseList(responses);
    }
}
