package com.neg.technology.human.resource.Department.validator;

import com.neg.technology.human.resource.Department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.Department.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DepartmentValidator {

    private final DepartmentService departmentService;

    public DepartmentValidator(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void validateCreate(CreateDepartmentRequest dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Department name must not be empty");
        }
        if (departmentService.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Department name already exists");
        }
    }

    public void validateUpdate(UpdateDepartmentRequest dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Department name must not be empty");
        }
        departmentService.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(dto.getId())) {
                throw new IllegalArgumentException("Department name already exists");
            }
        });
    }
}