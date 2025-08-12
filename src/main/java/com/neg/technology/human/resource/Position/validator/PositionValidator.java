package com.neg.technology.human.resource.Position.validator;

import com.neg.technology.human.resource.Position.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.Position.model.request.UpdatePositionRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PositionValidator {

    public void validateCreate(CreatePositionRequest dto) {
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        if (dto.getBaseSalary() != null && dto.getBaseSalary().signum() == -1) {
            throw new IllegalArgumentException("Base salary must be zero or positive");
        }
    }

    public void validateUpdate(UpdatePositionRequest dto, @NotNull Long id) {
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        if (dto.getBaseSalary() != null && dto.getBaseSalary().signum() == -1) {
            throw new IllegalArgumentException("Base salary must be zero or positive");
        }
    }
}
