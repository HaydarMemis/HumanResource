package com.neg.technology.human.resource.LeaveBalance.validator;

import com.neg.technology.human.resource.dto.create.CreateLeaveBalanceRequestDTO;
import com.neg.technology.human.resource.dto.update.UpdateLeaveBalanceRequestDTO;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.entity.LeaveType;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.repository.LeaveTypeRepository;
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

    public void validateCreateDTO(CreateLeaveBalanceRequestDTO dto) {
        validateAndGetEmployee(dto.getEmployeeId());
        validateAndGetLeaveType(dto.getLeaveTypeId());
    }

    public void validateUpdateDTO(UpdateLeaveBalanceRequestDTO dto) {
        if (dto.getEmployeeId() != null) {
            validateAndGetEmployee(dto.getEmployeeId());
        }
        if (dto.getLeaveTypeId() != null) {
            validateAndGetLeaveType(dto.getLeaveTypeId());
        }
    }
}