package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.*;
import com.neg.hr.human.resource.entity.Department;

public class DepartmentMapper {

    public static DepartmentDTO toDTO(Department department) {
        if (department == null) return null;
        return new DepartmentDTO(
                department.getId(),
                department.getName(),
                department.getLocation()
        );
    }

    public static Department toEntity(CreateDepartmentDTO dto) {
        if (dto == null) return null;
        return Department.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .build();
    }

    public static void updateEntity(Department department, UpdateDepartmentDTO dto) {
        if (department == null || dto == null) return;
        if (dto.getName() != null) department.setName(dto.getName());
        if (dto.getLocation() != null) department.setLocation(dto.getLocation());
    }
}
