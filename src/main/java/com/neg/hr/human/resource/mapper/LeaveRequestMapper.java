package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.create.CreateLeaveRequestRequestDTO;
import com.neg.hr.human.resource.dto.LeaveRequestEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveRequestRequestDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveRequest;
import com.neg.hr.human.resource.entity.LeaveType;

public class LeaveRequestMapper {

    public static LeaveRequestEntityDTO toDTO(LeaveRequest request) {
        return LeaveRequestEntityDTO.builder()
                .id(request.getId())
                .employeeFirstName(request.getEmployee().getPerson().getFirstName())
                .employeeLastName(request.getEmployee().getPerson().getLastName())
                .leaveTypeName(request.getLeaveType().getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .requestedDays(request.getRequestedDays())
                .status(request.getStatus())
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

    public static LeaveRequest toEntity(CreateLeaveRequestRequestDTO dto, Employee employee, LeaveType leaveType, Employee approver) {
        return LeaveRequest.builder()
                .employee(employee)
                .leaveType(leaveType)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .requestedDays(dto.getRequestedDays())
                .status(dto.getStatus())
                .reason(dto.getReason())
                .approvedBy(approver)
                .build();
    }

    public static void updateEntity(LeaveRequest entity, UpdateLeaveRequestRequestDTO dto, Employee employee, LeaveType leaveType, Employee approver) {
        if (employee != null) entity.setEmployee(employee);
        if (leaveType != null) entity.setLeaveType(leaveType);
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) entity.setEndDate(dto.getEndDate());
        if (dto.getRequestedDays() != null) entity.setRequestedDays(dto.getRequestedDays());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getReason() != null) entity.setReason(dto.getReason());
        if (approver != null) entity.setApprovedBy(approver);
        if (dto.getApprovedAt() != null) entity.setApprovedAt(dto.getApprovedAt());
        if (dto.getApprovalNote() != null) entity.setApprovalNote(dto.getApprovalNote());
        if (dto.getIsCancelled() != null) entity.setIsCancelled(dto.getIsCancelled());
        if (dto.getCancelledAt() != null) entity.setCancelledAt(dto.getCancelledAt());
        if (dto.getCancellationReason() != null) entity.setCancellationReason(dto.getCancellationReason());
    }
}
