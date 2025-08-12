package com.neg.technology.human.resource.Position.model.mapper;

import com.neg.technology.human.resource.Position.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.Position.model.response.PositionResponse;
import com.neg.technology.human.resource.Position.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.Position.model.entity.Position;

public class PositionMapper {

    public static PositionResponse toDTO(Position position) {
        if (position == null) return null;
        return new PositionResponse(
                position.getId(),
                position.getTitle(),
                position.getBaseSalary()
        );
    }

    public static Position toEntity(CreatePositionRequest dto) {
        if (dto == null) return null;
        return Position.builder()
                .title(dto.getTitle())
                .baseSalary(dto.getBaseSalary())
                .build();
    }

    public static void updateEntity(Position position, UpdatePositionRequest dto) {
        if (position == null || dto == null) return;
        if (dto.getTitle() != null) position.setTitle(dto.getTitle());
        if (dto.getBaseSalary() != null) position.setBaseSalary(dto.getBaseSalary());
    }
}
