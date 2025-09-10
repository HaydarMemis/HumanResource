package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.repository.LeaveRequestRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestValidator {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeavePolicyService leavePolicyService;

    public void validateCreateDTO(CreateLeaveRequestRequest dto) {
        validateCommon(dto.getEmployeeId(), dto.getLeaveTypeId(), dto.getStartDate(), dto.getEndDate());

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ValidationException("Çalışan bulunamadı"));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ValidationException("İzin tipi bulunamadı"));

        // Total izin hakkını policy servisinden al
        int maxDays = leavePolicyService.getLeaveDaysByPolicy(employee, leaveType, dto.getStartDate().getYear());

        long requestedDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (requestedDays > maxDays) {
            throw new ValidationException("Talep edilen gün sayısı izin hakkını aşıyor. Kalan: " + maxDays);
        }

        // Employee aktif mi ve cinsiyet kuralı
        if (!Boolean.TRUE.equals(employee.getIsActive())) {
            throw new ValidationException("Pasif çalışanlar izin talep edemez");
        }
        if (leaveType.getGenderRequired() != null) {
            LeaveType.Gender employeeGender = LeaveType.Gender.valueOf(employee.getPerson().getGender().toUpperCase());
            if (!employeeGender.equals(leaveType.getGenderRequired())) {
                throw new ValidationException("Bu izin tipi sadece " +
                        leaveType.getGenderRequired().name().toLowerCase() + " çalışanlara tanımlıdır.");
            }
        }
    }

    public void validateUpdateDTO(UpdateLeaveRequestRequest dto) {
        if (dto.getEmployeeId() != null && dto.getLeaveTypeId() != null &&
                dto.getStartDate() != null && dto.getEndDate() != null) {

            // Builder ile CreateLeaveRequestRequest oluştur
            CreateLeaveRequestRequest tempDto = CreateLeaveRequestRequest.builder()
                    .employeeId(dto.getEmployeeId())
                    .leaveTypeId(dto.getLeaveTypeId())
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .build();

            validateCreateDTO(tempDto);
        }
    }

    private void validateCommon(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate) {
        if (employeeId == null || leaveTypeId == null)
            throw new ValidationException("Çalışan ID ve İzin Tipi ID zorunludur");
        if (startDate == null || endDate == null)
            throw new ValidationException("Başlangıç ve Bitiş tarihi zorunludur");
        if (startDate.isAfter(endDate))
            throw new ValidationException("Başlangıç tarihi, bitiş tarihinden sonra olamaz");
    }

    public void validateEmployeeYearRequest(EmployeeYearRequest request) {
        if (request.getEmployeeId() == null)
            throw new ValidationException("Çalışan ID zorunludur");
        if (request.getYear() == null || request.getYear() < 2000)
            throw new ValidationException("Yıl geçersiz");
    }
}
