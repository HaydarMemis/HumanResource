package com.neg.technology.human.resource.LeaveRequest.validator;

import com.neg.technology.human.resource.dto.create.CreateLeaveRequestRequestDTO;
import com.neg.technology.human.resource.dto.update.UpdateLeaveRequestRequestDTO;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.LeaveBalance.model.entity.LeaveBalance;
import com.neg.technology.human.resource.LeaveType.model.entity.LeaveType;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.LeaveBalance.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.LeaveType.repository.LeaveTypeRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class LeaveRequestValidator {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public void validateCreateDTO(CreateLeaveRequestRequestDTO dto) {
        validateCommon(dto.getEmployeeId(), dto.getLeaveTypeId(), dto.getStartDate(), dto.getEndDate());

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ValidationException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ValidationException("Leave type not found"));

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeId(employee.getId(), leaveType.getId())
                .orElseThrow(() -> new ValidationException("Leave balance not found"));

        long requestedDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (requestedDays > balance.getAmount().intValue()) {
            throw new ValidationException("Requested days exceed leave balance");
        }

        validateExtraRules(employee, leaveType, dto.getStartDate(), dto.getEndDate());
    }

    public void validateUpdateDTO(UpdateLeaveRequestRequestDTO dto) {
        if (dto.getEmployeeId() != null && dto.getLeaveTypeId() != null &&
                dto.getStartDate() != null && dto.getEndDate() != null) {

            validateCommon(dto.getEmployeeId(), dto.getLeaveTypeId(), dto.getStartDate(), dto.getEndDate());

            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ValidationException("Employee not found"));

            LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                    .orElseThrow(() -> new ValidationException("Leave type not found"));

            validateExtraRules(employee, leaveType, dto.getStartDate(), dto.getEndDate());
        }
    }

    private void validateCommon(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate) {
        if (employeeId == null || leaveTypeId == null) {
            throw new ValidationException("Employee ID and Leave Type ID are required");
        }

        if (startDate == null || endDate == null) {
            throw new ValidationException("Start date and End date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }
    }

    private void validateExtraRules(
            Employee employee,
            LeaveType leaveType,
            LocalDate startDate,
            LocalDate endDate) {
        if (!Boolean.TRUE.equals(employee.getIsActive())) {
            throw new ValidationException("Inactive employees cannot request leave");
        }

        LeaveType.Gender requiredGender = leaveType.getGenderRequired();
        String employeeGenderStr = employee.getPerson().getGender();

        if (requiredGender != null) {
            if (employeeGenderStr == null) {
                throw new ValidationException("Employee gender is not specified.");
            }

            try {
                LeaveType.Gender employeeGender = LeaveType.Gender.valueOf(employeeGenderStr.toUpperCase());

                if (!requiredGender.equals(employeeGender)) {
                    throw new ValidationException("This leave type is restricted to " +
                            requiredGender.name().toLowerCase() + " employees only.");
                }

            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid gender value for employee: " + employeeGenderStr);
            }
        }


        LocalDate now = LocalDate.now();
        long daysUntilStart = ChronoUnit.DAYS.between(now, startDate);

        if (leaveType.getValidAfterDays() != null && daysUntilStart < leaveType.getValidAfterDays()) {
            throw new ValidationException("Leave request too early");
        }

        if (leaveType.getValidUntilDays() != null && daysUntilStart > leaveType.getValidUntilDays()) {
            throw new ValidationException("Leave request too far in advance");
        }
    }
}