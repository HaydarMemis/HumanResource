package com.neg.technology.human.resource.department.validator;

import com.neg.technology.human.resource.department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.department.service.DepartmentService;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
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
        NameRequest nameRequest = new NameRequest();
        nameRequest.setName(dto.getName());
        if (departmentService.existsByName(nameRequest)) {
            throw new IllegalArgumentException("Department name already exists");
        }
    }

    public void validateUpdate(UpdateDepartmentRequest dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Department name must not be empty");
        }
        NameRequest nameRequest = new NameRequest();
        nameRequest.setName(dto.getName());

        if (departmentService.existsByName(nameRequest)) {
            // Servis getDepartmentByName ile mevcut department'ı çekiyoruz
            var existing = departmentService.getDepartmentByName(nameRequest);
            if (!existing.getId().equals(dto.getId())) {
                throw new IllegalArgumentException("Department name already exists");
            }
        }
    }
}
