package com.neg.technology.human.resource.Department.model.mapper;

import com.neg.technology.human.resource.Department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.Department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.entity.Department;
import jakarta.validation.Valid;

public class DepartmentMapper {

    public static DepartmentResponse toDTO(Department department) {
        if (department == null) return null;
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getLocation()
        );
    }

    public static Department toEntity(@Valid CreateDepartmentRequest dto) {
        if (dto == null) return null;
        return Department.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .build();
    }

    public static Department toEntity(@Valid UpdateDepartmentRequest dto) {
        if (dto == null) return null;
        return Department.builder()
                .id(dto.getId())
                .name(dto.getName())
                .location(dto.getLocation())
                .build();
    }

    public static void updateEntity(Department department, UpdateDepartmentRequest dto) {
        if (department == null || dto == null) return;
        if (dto.getName() != null) department.setName(dto.getName());
        if (dto.getLocation() != null) department.setLocation(dto.getLocation());
    }
}
