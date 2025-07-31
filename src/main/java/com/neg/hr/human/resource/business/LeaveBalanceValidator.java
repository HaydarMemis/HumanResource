package com.neg.hr.human.resource.business;

import com.neg.hr.human.resource.dto.CreateLeaveBalanceDTO;
import com.neg.hr.human.resource.dto.UpdateLeaveBalanceDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class LeaveBalanceValidator {
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveBalanceValidator(EmployeeRepository employeeRepository, LeaveTypeRepository leaveTypeRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public Employee validateAndGetEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID: " + employeeId));
    }

    public LeaveType validateAndGetLeaveType(Long leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type ID: " + leaveTypeId));
    }

    public void validateCreateDTO(CreateLeaveBalanceDTO dto) {
        validateAndGetEmployee(dto.getEmployeeId());
        validateAndGetLeaveType(dto.getLeaveTypeId());
    }

    public void validateUpdateDTO(UpdateLeaveBalanceDTO dto) {
        if (dto.getEmployeeId() != null) {
            validateAndGetEmployee(dto.getEmployeeId());
        }
        if (dto.getLeaveTypeId() != null) {
            validateAndGetLeaveType(dto.getLeaveTypeId());
        }
    }
}
