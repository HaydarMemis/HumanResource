package com.neg.hr.human.resource.business;

import com.neg.hr.human.resource.dto.CreateEmployeeProjectDTO;
import com.neg.hr.human.resource.dto.UpdateEmployeeProjectDTO;
import com.neg.hr.human.resource.repository.EmployeeProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProjectValidator {
    private final EmployeeProjectRepository employeeProjectRepository;

    public EmployeeProjectValidator(EmployeeProjectRepository employeeProjectRepository) {
        this.employeeProjectRepository = employeeProjectRepository;
    }

    public void validateCreateDTO(CreateEmployeeProjectDTO dto) {
        boolean exists = employeeProjectRepository
                .existsByEmployee_IdAndProject_Id(dto.getEmployeeId(), dto.getProjectId());

        if (exists) {
            throw new IllegalStateException("This employee is already assigned to this project.");
        }
    }

    public void validateUpdateDTO(Long id,UpdateEmployeeProjectDTO dto) {
        if(dto.getEmployeeId()!=null && dto.getProjectId()!=null) {
            boolean duplicateExists = employeeProjectRepository
                    .findByEmployeeId(dto.getEmployeeId())
                    .stream()
                    .anyMatch(ep -> ep.getProject().getId().equals(dto.getProjectId()) && !ep.getId().equals(id));

            if (duplicateExists) {
                throw new IllegalStateException("Another entry is already assigns the employee to this project.");
            }
        }
    }
}
