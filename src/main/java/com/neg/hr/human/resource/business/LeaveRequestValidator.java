package com.neg.hr.human.resource.business;

import com.neg.hr.human.resource.dto.CreateLeaveRequestDTO;
import com.neg.hr.human.resource.dto.UpdateLeaveRequestDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveBalance;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.LeaveBalanceRepository;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class LeaveRequestValidator {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    public LeaveRequestValidator(EmployeeRepository employeeRepository,
                                 LeaveTypeRepository leaveTypeRepository,
                                 LeaveBalanceRepository leaveBalanceRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    public void validate(CreateLeaveRequestDTO dto) {
        validateCommonRules(
                dto.getEmployeeId(),
                dto.getLeaveTypeId(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getApprovedById()
        );
    }

    public void validate(UpdateLeaveRequestDTO dto) {
        validateCommonRules(
                dto.getEmployeeId(),
                dto.getLeaveTypeId(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getApprovedById()
        );
    }

    private void validateCommonRules(Long employeeId, Long leaveTypeId,
                                     LocalDate startDate, LocalDate endDate, Long approvedById) {
        // Null checks
        if (employeeId == null || leaveTypeId == null || startDate == null || endDate == null || approvedById == null) {
            throw new ValidationException("Required fields must not be null.");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ValidationException("Employee not found."));

        if (!employee.getIsActive()) {
            throw new ValidationException("Inactive employees cannot make leave requests.");
        }

        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ValidationException("Leave type not found."));

        // Gender requirement
        if (leaveType.getGenderRequired() != null &&
                !leaveType.getGenderRequired().equalsIgnoreCase(employee.getPerson().getGender())) {
            throw new ValidationException("Employee does not meet the gender requirement for this leave type.");
        }

        // Valid leave window
        if (!leaveType.isDateWithinValidRange(startDate)) { // assume helper inside LeaveType
            throw new ValidationException("Leave start date is not within the valid range of this leave type.");
        }

        // Date order
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date cannot be before start date.");
        }

        // Balance check
        Optional<LeaveBalance> optionalBalance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId);

        if (optionalBalance.isEmpty()) {
            throw new ValidationException("No leave balance found for this leave type and employee.");
        }

        LeaveBalance leaveBalance = optionalBalance.get();
        long daysRequested = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (daysRequested > leaveBalance.getAmount()) {
            throw new ValidationException("Leave request exceeds available leave balance.");
        }
    }
}
