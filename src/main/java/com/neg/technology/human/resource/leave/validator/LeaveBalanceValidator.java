package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class LeaveBalanceValidator {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeavePolicyService leavePolicyService;

    public Employee validateAndGetEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ValidationException("Geçersiz çalışan ID: " + employeeId));
    }

    public LeaveType validateAndGetLeaveType(Long leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ValidationException("Geçersiz izin tipi ID: " + leaveTypeId));
    }

    /**
     * CreateLeaveBalanceRequest için validasyon
     */
    public void validateCreateDTO(CreateLeaveBalanceRequest dto) {
        Employee employee = validateAndGetEmployee(dto.getEmployeeId());
        LeaveType leaveType = validateAndGetLeaveType(dto.getLeaveTypeId());

        LocalDate birthDate = employee.getPerson() != null ? employee.getPerson().getBirthDate() : null;
        String gender = employee.getPerson() != null ? employee.getPerson().getGender() : null;
        LocalDateTime startDateTime = employee.getEmploymentStartDate();
        LocalDate startDate = startDateTime != null ? startDateTime.toLocalDate() : null;

        if (birthDate == null) throw new ValidationException("Çalışanın doğum tarihi bilgisi eksik.");
        if (startDate == null) throw new ValidationException("Çalışanın işe başlama tarihi bilgisi eksik.");
        if (gender == null) throw new ValidationException("Çalışanın cinsiyet bilgisi eksik.");

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) throw new ValidationException("18 yaşından küçük çalışanlara izin eklenemez.");

        // --- Policy kontrolü ---
        int year = dto.getDate() != null ? dto.getDate() : LocalDate.now().getYear();
        int maxDays = leavePolicyService.getLeaveDaysByPolicy(employee, leaveType, year);

        // multiplePregnancy sadece maternity leave için geçerli
        if ("maternity leave".equalsIgnoreCase(leaveType.getName()) && dto.getMultiplePregnancy() != null) {
            if (dto.getMultiplePregnancy()) {
                maxDays = Math.max(maxDays, 140);
            } else {
                maxDays = Math.max(maxDays, 112);
            }
        }

        if (dto.getAmount() != null && dto.getAmount().compareTo(BigDecimal.valueOf(maxDays)) > 0) {
            throw new ValidationException(
                    leaveType.getName() + " için maksimum izin gün sayısı aşılıyor. Maks: " + maxDays
            );
        }
    }

    /**
     * UpdateLeaveBalanceRequest için validasyon
     */
    public void validateUpdateDTO(UpdateLeaveBalanceRequest dto) {
        if (dto.getEmployeeId() != null && dto.getLeaveTypeId() != null) {
            CreateLeaveBalanceRequest createDto = new CreateLeaveBalanceRequest();
            createDto.setEmployeeId(dto.getEmployeeId());
            createDto.setLeaveTypeId(dto.getLeaveTypeId());
            createDto.setAmount(dto.getAmount());
            createDto.setDate(dto.getDate());
            // multiplePregnancy sadece ilgili izin tipleri için geçerli, null olabilir
            createDto.setMultiplePregnancy(false);
            validateCreateDTO(createDto);
        }
    }
}
