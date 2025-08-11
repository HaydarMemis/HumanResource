package com.neg.technology.human.resource.mapper;

import com.neg.technology.human.resource.dto.create.CreateLeaveTypeRequestDTO;
import com.neg.technology.human.resource.dto.entity.LeaveTypeEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateLeaveTypeRequestDTO;
import com.neg.technology.human.resource.LeaveType.entity.LeaveType;

public class LeaveTypeMapper {

    public static LeaveTypeEntityDTO toDTO(LeaveType leaveType) {
        if (leaveType == null) return null;

        return LeaveTypeEntityDTO.builder()
                .id(leaveType.getId())
                .name(leaveType.getName())
                .isAnnual(leaveType.getIsAnnual())
                .genderRequired(leaveType.getGenderRequired() != null ? leaveType.getGenderRequired().name() : null)
                .isUnpaid(leaveType.getIsUnpaid())
                .build();
    }

    public static LeaveType toEntity(CreateLeaveTypeRequestDTO dto) {
        if (dto == null) return null;

        return LeaveType.builder()
                .name(dto.getName())
                .isAnnual(dto.getIsAnnual())
                .genderRequired(dto.getGenderRequired())
                .defaultDays(dto.getDefaultDays())
                .validAfterDays(dto.getValidAfterDays())
                .validUntilDays(dto.getValidUntilDays())
                .isUnpaid(dto.getIsUnpaid())
                .resetPeriod(dto.getResetPeriod())
                .borrowableLimit(dto.getBorrowableLimit())
                .build();
    }

    public static void updateEntity(LeaveType leaveType, UpdateLeaveTypeRequestDTO dto) {
        if (leaveType == null || dto == null) return;

        if (dto.getName() != null) leaveType.setName(dto.getName());
        if (dto.getIsAnnual() != null) leaveType.setIsAnnual(dto.getIsAnnual());
        if (dto.getGenderRequired() != null) leaveType.setGenderRequired(dto.getGenderRequired());
        if (dto.getDefaultDays() != null) leaveType.setDefaultDays(dto.getDefaultDays());
        if (dto.getValidAfterDays() != null) leaveType.setValidAfterDays(dto.getValidAfterDays());
        if (dto.getValidUntilDays() != null) leaveType.setValidUntilDays(dto.getValidUntilDays());
        if (dto.getIsUnpaid() != null) leaveType.setIsUnpaid(dto.getIsUnpaid());
        if (dto.getResetPeriod() != null) leaveType.setResetPeriod(dto.getResetPeriod());
        if (dto.getBorrowableLimit() != null) leaveType.setBorrowableLimit(dto.getBorrowableLimit());
    }
}
