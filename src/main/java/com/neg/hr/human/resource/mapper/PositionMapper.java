package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.*;
import com.neg.hr.human.resource.dto.create.CreatePositionDTO;
import com.neg.hr.human.resource.dto.update.UpdatePositionDTO;
import com.neg.hr.human.resource.entity.Position;

public class PositionMapper {

    public static PositionDTO toDTO(Position position) {
        if (position == null) return null;
        return new PositionDTO(
                position.getId(),
                position.getTitle(),
                position.getBaseSalary()
        );
    }

    public static Position toEntity(CreatePositionDTO dto) {
        if (dto == null) return null;
        return Position.builder()
                .title(dto.getTitle())
                .baseSalary(dto.getBaseSalary())
                .build();
    }

    public static void updateEntity(Position position, UpdatePositionDTO dto) {
        if (position == null || dto == null) return;
        if (dto.getTitle() != null) position.setTitle(dto.getTitle());
        if (dto.getBaseSalary() != null) position.setBaseSalary(dto.getBaseSalary());
    }
}
