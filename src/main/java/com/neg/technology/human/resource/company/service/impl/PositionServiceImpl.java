package com.neg.technology.human.resource.company.service.impl;

import com.neg.technology.human.resource.company.model.entity.Position;
import com.neg.technology.human.resource.company.model.mapper.PositionMapper;
import com.neg.technology.human.resource.company.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.company.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.company.model.response.PositionResponse;
import com.neg.technology.human.resource.company.model.response.PositionResponseList;
import com.neg.technology.human.resource.company.repository.PositionRepository;
import com.neg.technology.human.resource.company.service.PositionService;
import com.neg.technology.human.resource.company.validator.PositionValidator;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.SalaryRequest;
import com.neg.technology.human.resource.utility.module.entity.request.TitleRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final PositionValidator positionValidator;
    private final PositionMapper positionMapper;

    public PositionServiceImpl(PositionRepository positionRepository,
                               PositionValidator positionValidator,
                               PositionMapper positionMapper) {
        this.positionRepository = positionRepository;
        this.positionValidator = positionValidator;
        this.positionMapper = positionMapper;
    }

    @Override
    public Mono<PositionResponseList> getAllPositions() {
        return Mono.fromCallable((Callable<PositionResponseList>) () ->
                new PositionResponseList(
                        positionRepository.findAll()
                                .stream()
                                .map(positionMapper::toDTO)
                                .toList()
                )
        );
    }

    @Override
    public Mono<PositionResponse> getPositionById(IdRequest request) {
        return Mono.fromCallable((Callable<PositionResponse>) () ->
                positionRepository.findById(request.getId())
                        .map(positionMapper::toDTO)
                        .orElseThrow(ResourceNotFoundException::positionNotFound)
        );
    }

    @Override
    public Mono<PositionResponse> createPosition(CreatePositionRequest request) {
        return Mono.fromCallable((Callable<PositionResponse>) () -> {
            positionValidator.validateCreate(request);
            Position position = positionMapper.toEntity(request);
            Position saved = positionRepository.save(position);
            Logger.logCreated(Position.class, saved.getId(), saved.getTitle());
            return positionMapper.toDTO(saved);
        });
    }

    @Override
    public Mono<PositionResponse> updatePosition(UpdatePositionRequest request) {
        return Mono.fromCallable((Callable<PositionResponse>) () -> {
            if (!positionRepository.existsById(request.getId())) {
                throw ResourceNotFoundException.positionNotFound();
            }
            positionValidator.validateUpdate(request);
            Position existing = positionRepository.findById(request.getId())
                    .orElseThrow(ResourceNotFoundException::positionNotFound);
            positionMapper.updateEntity(existing, request);
            Position updated = positionRepository.save(existing);
            Logger.logUpdated(Position.class, updated.getId(), updated.getTitle());
            return positionMapper.toDTO(updated);
        });
    }

    @Override
    public Mono<Void> deletePosition(IdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!positionRepository.existsById(request.getId())) {
                throw ResourceNotFoundException.positionNotFound();
            }
            positionRepository.deleteById(request.getId());
            Logger.logDeleted(Position.class, request.getId());
        });
    }

    @Override
    public Mono<PositionResponse> getPositionByTitle(TitleRequest request) {
        return Mono.fromCallable((Callable<PositionResponse>) () ->
                positionRepository.findByTitle(request.getTitle())
                        .map(positionMapper::toDTO)
                        .orElseThrow(ResourceNotFoundException::positionTitleNotFound)
        );
    }

    @Override
    public Mono<Boolean> existsByTitle(TitleRequest request) {
        return Mono.fromCallable(() -> positionRepository.existsByTitle(request.getTitle()));
    }

    @Override
    public Mono<PositionResponseList> getPositionsByBaseSalary(SalaryRequest request) {
        return Mono.fromCallable((Callable<PositionResponseList>) () ->
                new PositionResponseList(
                        positionRepository.findByBaseSalaryGreaterThanEqual(request.getSalary())
                                .stream()
                                .map(positionMapper::toDTO)
                                .toList()
                )
        );
    }

    // --- Utility reactive methods ---
    @Override
    public Mono<Position> save(Position position) {
        return Mono.fromCallable(() -> positionRepository.save(position));
    }

    @Override
    public Mono<Position> findById(Long id) {
        return Mono.fromCallable(() ->
                positionRepository.findById(id)
                        .orElseThrow(ResourceNotFoundException::positionNotFound)
        );
    }

    @Override
    public Flux<Position> findAll() {
        return Flux.defer(() -> Flux.fromIterable(positionRepository.findAll()));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> {
            if (!positionRepository.existsById(id)) {
                throw ResourceNotFoundException.positionNotFound();
            }
            positionRepository.deleteById(id);
        });
    }

    @Override
    public Mono<Position> update(Long id, Position position) {
        return Mono.fromCallable(() -> {
            Position existing = positionRepository.findById(id)
                    .orElseThrow(ResourceNotFoundException::positionNotFound);
            existing.setTitle(position.getTitle());
            existing.setBaseSalary(position.getBaseSalary());
            return positionRepository.save(existing);
        });
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return Mono.fromCallable(() -> positionRepository.existsById(id));
    }

    @Override
    public Mono<Boolean> existsByTitle(String title) {
        return Mono.fromCallable(() -> positionRepository.existsByTitle(title));
    }

    @Override
    public Flux<Position> findByBaseSalaryGreaterThanEqual(BigDecimal salary) {
        return Flux.defer(() -> Flux.fromIterable(positionRepository.findByBaseSalaryGreaterThanEqual(salary)));
    }

    @Override
    public Mono<Position> findByTitle(String title) {
        return Mono.fromCallable(() ->
                positionRepository.findByTitle(title)
                        .orElseThrow(ResourceNotFoundException::positionTitleNotFound)
        );
    }
}
