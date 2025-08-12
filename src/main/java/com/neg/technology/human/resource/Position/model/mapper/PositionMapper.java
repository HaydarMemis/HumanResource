package com.neg.technology.human.resource.Position.model.mapper;

import com.neg.technology.human.resource.Position.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.Position.model.response.PositionResponse;
import com.neg.technology.human.resource.Position.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.Position.model.entity.Position;
import org.springframework.stereotype.Component;

@Component
public class PositionMapper {

    public PositionResponse toDTO(Position position) {
        if (position == null) return null;
        return new PositionResponse(
                position.getId(),
                position.getTitle(),
                position.getBaseSalary()
        );
    }

    public Position toEntity(CreatePositionRequest dto) {
        if (dto == null) return null;
        return Position.builder()
                .title(dto.getTitle())
                .baseSalary(dto.getBaseSalary())
                .build();
    }

    public void updateEntity(Position position, UpdatePositionRequest dto) {
        if (position == null || dto == null) return;
        if (dto.getTitle() != null) position.setTitle(dto.getTitle());
        if (dto.getBaseSalary() != null) position.setBaseSalary(dto.getBaseSalary());
    }
}
