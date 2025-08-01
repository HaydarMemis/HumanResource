package com.neg.hr.human.resource.business;

import com.neg.hr.human.resource.dto.CreateDepartmentDTO;
import com.neg.hr.human.resource.dto.UpdateDepartmentDTO;
import com.neg.hr.human.resource.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DepartmentValidator {

    private final DepartmentService departmentService;

    public DepartmentValidator(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void validateCreate(CreateDepartmentDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Department name must not be empty");
        }
        if (departmentService.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Department name already exists");
        }
    }

    public void validateUpdate(UpdateDepartmentDTO dto, Long id) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Department name must not be empty");
        }
        departmentService.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Department name already exists");
            }
        });
    }
}
