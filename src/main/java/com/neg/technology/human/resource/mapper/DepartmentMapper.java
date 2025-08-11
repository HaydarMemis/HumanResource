package com.neg.technology.human.resource.mapper;

import com.neg.technology.human.resource.dto.create.CreateDepartmentRequestDTO;
import com.neg.technology.human.resource.dto.entity.DepartmentEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateDepartmentRequestDTO;
import com.neg.technology.human.resource.entity.Department;

public class DepartmentMapper {

    public static DepartmentEntityDTO toDTO(Department department) {
        if (department == null) return null;
        return new DepartmentEntityDTO(
                department.getId(),
                department.getName(),
                department.getLocation()
        );
    }

    public static Department toEntity(CreateDepartmentRequestDTO dto) {
        if (dto == null) return null;
        return Department.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .build();
    }

    public static void updateEntity(Department department, UpdateDepartmentRequestDTO dto) {
        if (department == null || dto == null) return;
        if (dto.getName() != null) department.setName(dto.getName());
        if (dto.getLocation() != null) department.setLocation(dto.getLocation());
    }
}
