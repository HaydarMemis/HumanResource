package com.neg.technology.human.resource.employee_project.validator;

import com.neg.technology.human.resource.employee_project.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee_project.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee_project.repository.EmployeeProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProjectValidator {
    private final EmployeeProjectRepository employeeProjectRepository;

    public EmployeeProjectValidator(EmployeeProjectRepository employeeProjectRepository) {
        this.employeeProjectRepository = employeeProjectRepository;
    }

    public void validateCreateDTO(CreateEmployeeProjectRequest dto) {
        boolean exists = employeeProjectRepository
                .existsByEmployee_IdAndProject_Id(dto.getEmployeeId(), dto.getProjectId());

        if (exists) {
            throw new IllegalStateException("This employee is already assigned to this project.");
        }
    }

    public void validateUpdateDTO(Long id, UpdateEmployeeProjectRequest dto) {
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