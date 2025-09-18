package com.neg.technology.human.resource.leave.model.mapper;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.leave.model.entity.LeaveRequest;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.enums.LeaveStatus;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponse;

public class LeaveRequestMapper {
    private LeaveRequestMapper(){}

    public static LeaveRequestResponse toDTO(LeaveRequest request) {
        return LeaveRequestResponse.builder()
                .id(request.getId())
                .employeeFirstName(request.getEmployee().getPerson().getFirstName())
                .employeeLastName(request.getEmployee().getPerson().getLastName())
                .leaveTypeName(request.getLeaveType().getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .requestedDays(request.getRequestedDays())
                .status(request.getStatus() != null ? request.getStatus().name() : null)
                .reason(request.getReason())
                .approvedByFirstName(request.getApprovedBy().getPerson().getFirstName())
                .approvedByLastName(request.getApprovedBy().getPerson().getLastName())
                .approvedAt(request.getApprovedAt())
                .approvalNote(request.getApprovalNote())
                .isCancelled(request.getIsCancelled())
                .cancelledAt(request.getCancelledAt())
                .cancellationReason(request.getCancellationReason())
                .build();
    }

    public static LeaveRequest toEntity(CreateLeaveRequestRequest dto, Employee employee, LeaveType leaveType, Employee approver) {
        return LeaveRequest.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .requestedDays(dto.getRequestedDays())
                .status(dto.getStatus()) // No conversion needed, type is already LeaveStatus
                .reason(dto.getReason())
                .approvedBy(approver)
                .build();
    }

    public static void updateEntity(LeaveRequest entity, UpdateLeaveRequest dto, Employee employee, LeaveType leaveType, Employee approver) {
        if (employee != null) entity.setEmployee(employee);
        if (leaveType != null) entity.setLeaveType(leaveType);
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) entity.setEndDate(dto.getEndDate());
        if (dto.getRequestedDays() != null) entity.setRequestedDays(dto.getRequestedDays());
        if (dto.getStatus() != null) {
            entity.setStatus(LeaveStatus.fromString(dto.getStatus()));
        }

        if (dto.getReason() != null) entity.setReason(dto.getReason());
        if (approver != null) entity.setApprovedBy(approver);
        if (dto.getApprovedAt() != null) entity.setApprovedAt(dto.getApprovedAt());
        if (dto.getApprovalNote() != null) entity.setApprovalNote(dto.getApprovalNote());
        if (dto.getIsCancelled() != null) entity.setIsCancelled(dto.getIsCancelled());
        if (dto.getCancelledAt() != null) entity.setCancelledAt(dto.getCancelledAt());
        if (dto.getCancellationReason() != null) entity.setCancellationReason(dto.getCancellationReason());
    }
}