package com.neg.hr.human.resource.validator;

import com.neg.hr.human.resource.dto.create.CreateDepartmentRequestDTO;
import com.neg.hr.human.resource.dto.update.UpdateDepartmentRequestDTO;
import com.neg.hr.human.resource.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DepartmentValidator {

    private final DepartmentService departmentService;

    public DepartmentValidator(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void validateCreate(CreateDepartmentRequestDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Department name must not be empty");
        }
        if (departmentService.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Department name already exists");
        }
    }

    public void validateUpdate(UpdateDepartmentRequestDTO dto) {  // Remove the id parameter
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Department name must not be empty");
        }
        departmentService.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(dto.getId())) {  // Use dto.getId() instead
                throw new IllegalArgumentException("Department name already exists");
            }
        });
    }
}
