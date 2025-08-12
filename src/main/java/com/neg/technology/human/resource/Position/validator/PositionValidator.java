package com.neg.technology.human.resource.Position.validator;

import com.neg.technology.human.resource.Position.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.Position.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.Position.service.PositionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PositionValidator {

    private final PositionService positionService;

    public PositionValidator(PositionService positionService) {
        this.positionService = positionService;
    }

    public void validateCreate(CreatePositionRequest dto) {
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        if (positionService.existsByTitle(dto.getTitle())) {
            throw new IllegalArgumentException("Title already exists");
        }
        if (dto.getBaseSalary() != null && dto.getBaseSalary().signum() == -1) {
            throw new IllegalArgumentException("Base salary must be zero or positive");
        }
    }

    public void validateUpdate(UpdatePositionRequest dto, Long id) {
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        positionService.findByTitle(dto.getTitle()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Title already exists");
            }
        });
        if (dto.getBaseSalary() != null && dto.getBaseSalary().signum() == -1) {
            throw new IllegalArgumentException("Base salary must be zero or positive");
        }
    }
}