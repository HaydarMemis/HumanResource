package com.neg.technology.human.resource.company.service;

import com.neg.technology.human.resource.company.model.entity.Position;
import com.neg.technology.human.resource.company.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.company.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.company.model.response.PositionResponse;
import com.neg.technology.human.resource.company.model.response.PositionResponseList;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.SalaryRequest;
import com.neg.technology.human.resource.utility.module.entity.request.TitleRequest;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface PositionService {
    PositionResponseList getAllPositions();

    Optional<Position> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Position> findByBaseSalaryGreaterThanEqual(java.math.BigDecimal salary);

    Position save(Position position);

    Optional<Position> findById(Long id);

    List<Position> findAll();

    void deleteById(Long id);

    Position update(Long id, Position position);

    boolean existsById(Long id);

    PositionResponse getPositionById(IdRequest request);

    PositionResponse createPosition(CreatePositionRequest request);

    PositionResponse updatePosition(UpdatePositionRequest request);

    void deletePosition(@Valid IdRequest request);

    PositionResponse getPositionByTitle(TitleRequest request);

    boolean existsByTitle(TitleRequest request);

    PositionResponseList getPositionsByBaseSalary(SalaryRequest request);

}
