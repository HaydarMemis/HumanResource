package com.neg.technology.human.resource.position.service;

import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.position.model.entity.Position;
import com.neg.technology.human.resource.position.model.mapper.PositionMapper;
import com.neg.technology.human.resource.position.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.position.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.position.model.response.PositionResponse;
import com.neg.technology.human.resource.position.model.response.PositionResponseList;
import com.neg.technology.human.resource.position.repository.PositionRepository;
import com.neg.technology.human.resource.position.validator.PositionValidator;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.SalaryRequest;
import com.neg.technology.human.resource.utility.module.entity.request.TitleRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PositionServiceImpl implements PositionService {
    private String message= "Position";

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
    public PositionResponseList getAllPositions() {
        List<PositionResponse> list = positionRepository.findAll()
                .stream()
                .map(positionMapper::toDTO)
                .toList();
        return new PositionResponseList(list);
    }


    @Override
    public Optional<Position> findByTitle(String title) {
        return positionRepository.findByTitle(title);
    }

    @Override
    public boolean existsByTitle(String title) {
        return positionRepository.existsByTitle(title);
    }

    @Override
    public List<Position> findByBaseSalaryGreaterThanEqual(BigDecimal salary) {
        return positionRepository.findByBaseSalaryGreaterThanEqual(salary);
    }

    @Override
    public Position save(Position position) {
        Position saved = positionRepository.save(position);
        Logger.logCreated(Position.class, saved.getId(), saved.getTitle());
        return saved;
    }

    @Override
    public Optional<Position> findById(Long id) {
        return positionRepository.findById(id);
    }

    @Override
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        if(!positionRepository.existsById(id)) {
            throw new ResourceNotFoundException(message, id);
        }
        positionRepository.deleteById(id);
        Logger.logDeleted(Position.class, id);
    }

    @Override
    public Position update(Long id, Position position) {
        Position existing = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(message, id));
        existing.setTitle(position.getTitle());
        existing.setBaseSalary(position.getBaseSalary());

        Position updated = positionRepository.save(existing);
        Logger.logUpdated(Position.class, updated.getId(), updated.getTitle());
        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return positionRepository.existsById(id);
    }

    @Override
    public PositionResponse getPositionById(IdRequest request) {
        Position position = positionRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(message, request.getId()));
        return positionMapper.toDTO(position);
    }

    @Override
    public PositionResponse createPosition(CreatePositionRequest request) {
        positionValidator.validateCreate(request);
        Position position = positionMapper.toEntity(request);
        Position saved = positionRepository.save(position);
        Logger.logCreated(Position.class, saved.getId(), saved.getTitle());
        return positionMapper.toDTO(saved);
    }

    @Override
    public PositionResponse updatePosition(UpdatePositionRequest request) {
        if (!positionRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException(message, request.getId());
        }
        positionValidator.validateUpdate(request);
        Position existing = positionRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(message, request.getId()));
        positionMapper.updateEntity(existing, request);
        Position updated = positionRepository.save(existing);
        Logger.logUpdated(Position.class, updated.getId(), updated.getTitle());
        return positionMapper.toDTO(updated);
    }

    @Override
    public void deletePosition(IdRequest request) {
        if (!positionRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException(message, request.getId());
        }
        positionRepository.deleteById(request.getId());
        Logger.logDeleted(Position.class, request.getId());
    }

    @Override
    public PositionResponse getPositionByTitle(TitleRequest request) {
        Position position = positionRepository.findByTitle(request.getTitle())
                .orElseThrow(() -> new ResourceNotFoundException(message, request.getTitle()));
        return positionMapper.toDTO(position);
    }

    @Override
    public boolean existsByTitle(TitleRequest request) {
        return positionRepository.existsByTitle(request.getTitle());
    }

    @Override
    public PositionResponseList getPositionsByBaseSalary(SalaryRequest request) {
        List<PositionResponse> list = positionRepository.findByBaseSalaryGreaterThanEqual(request.getSalary())
                .stream()
                .map(positionMapper::toDTO)
                .toList();
        return new PositionResponseList(list);
    }
}
