package com.neg.technology.human.resource.EmployeeProject.validator;

import com.neg.technology.human.resource.dto.create.CreateEmployeeProjectRequestDTO;
import com.neg.technology.human.resource.dto.update.UpdateEmployeeProjectRequestDTO;
import com.neg.technology.human.resource.EmployeeProject.repository.EmployeeProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProjectValidator {
    private final EmployeeProjectRepository employeeProjectRepository;

    public EmployeeProjectValidator(EmployeeProjectRepository employeeProjectRepository) {
        this.employeeProjectRepository = employeeProjectRepository;
    }

    public void validateCreateDTO(CreateEmployeeProjectRequestDTO dto) {
        boolean exists = employeeProjectRepository
                .existsByEmployee_IdAndProject_Id(dto.getEmployeeId(), dto.getProjectId());

        if (exists) {
            throw new IllegalStateException("This employee is already assigned to this project.");
        }
    }

    public void validateUpdateDTO(Long id, UpdateEmployeeProjectRequestDTO dto) {
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