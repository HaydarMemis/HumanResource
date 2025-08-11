package com.neg.technology.human.resource.validator;

import com.neg.technology.human.resource.dto.create.CreatePositionRequestDTO;
import com.neg.technology.human.resource.dto.update.UpdatePositionRequestDTO;
import com.neg.technology.human.resource.service.PositionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PositionValidator {

    private final PositionService positionService;

    public PositionValidator(PositionService positionService) {
        this.positionService = positionService;
    }

    public void validateCreate(CreatePositionRequestDTO dto) {
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

    public void validateUpdate(UpdatePositionRequestDTO dto, Long id) {
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