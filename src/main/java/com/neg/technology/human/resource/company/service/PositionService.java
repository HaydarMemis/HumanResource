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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface PositionService {

    Mono<PositionResponseList> getAllPositions();

    Mono<PositionResponse> getPositionById(IdRequest request);

    Mono<PositionResponse> createPosition(CreatePositionRequest request);

    Mono<PositionResponse> updatePosition(UpdatePositionRequest request);

    Mono<Void> deletePosition(@Valid IdRequest request);

    Mono<PositionResponse> getPositionByTitle(TitleRequest request);

    Mono<Boolean> existsByTitle(TitleRequest request);

    Mono<PositionResponseList> getPositionsByBaseSalary(SalaryRequest request);

    // Utility-level methods
    Mono<Position> save(Position position);

    Mono<Position> findById(Long id);

    Flux<Position> findAll();

    Mono<Void> deleteById(Long id);

    Mono<Position> update(Long id, Position position);

    Mono<Boolean> existsById(Long id);

    Mono<Boolean> existsByTitle(String title);

    Flux<Position> findByBaseSalaryGreaterThanEqual(BigDecimal salary);

    Mono<Position> findByTitle(String title);
}
