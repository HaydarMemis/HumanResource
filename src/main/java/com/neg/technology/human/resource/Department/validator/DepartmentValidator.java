package com.neg.technology.human.resource.Department.validator;

import com.neg.technology.human.resource.Department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.Department.service.DepartmentService;
import com.neg.technology.human.resource.Utility.request.NameRequest;
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
