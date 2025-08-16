package com.neg.technology.human.resource.leave_type.model.mapper;

import com.neg.technology.human.resource.leave_type.model.entity.LeaveType;
import com.neg.technology.human.resource.leave_type.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.leave_type.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.leave_type.model.response.LeaveTypeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LeaveTypeMapper {

    public LeaveTypeResponse toDTO(LeaveType leaveType) {
        if (leaveType == null) return null;

        return LeaveTypeResponse.builder()
                .id(leaveType.getId())
                .name(leaveType.getName())
                .isAnnual(leaveType.getIsAnnual())
                .genderRequired(leaveType.getGenderRequired() != null ? leaveType.getGenderRequired().name() : null)
                .isUnpaid(leaveType.getIsUnpaid())
                .defaultDays(leaveType.getDefaultDays())
                .validAfterDays(leaveType.getValidAfterDays())
                .validUntilDays(leaveType.getValidUntilDays())
                .borrowableLimit(leaveType.getBorrowableLimit())
                .resetPeriod(leaveType.getResetPeriod())
                .build();
    }

    // Alias: toResponse
    public LeaveTypeResponse toResponse(LeaveType leaveType) {
        return toDTO(leaveType);
    }

    // Liste dönüşümü
    public List<LeaveTypeResponse> toDTOList(List<LeaveType> leaveTypes) {
        return leaveTypes.stream()
                .map(this::toDTO)
                .toList();
    }

    // Alias: toResponseList
    public List<LeaveTypeResponse> toResponseList(List<LeaveType> leaveTypes) {
        return toDTOList(leaveTypes);
    }

    public LeaveType toEntity(CreateLeaveTypeRequest dto) {
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

    public void updateEntity(LeaveType leaveType, UpdateLeaveTypeRequest dto) {
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

    // Alias: updateEntityFromRequest (parametre sırasını senin koduna göre değiştirdim)
    public void updateEntityFromRequest(UpdateLeaveTypeRequest dto, LeaveType leaveType) {
        updateEntity(leaveType, dto);
    }
}
