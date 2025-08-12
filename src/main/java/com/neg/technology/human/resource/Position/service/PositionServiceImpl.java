package com.neg.technology.human.resource.Position.service;

import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Position.model.entity.Position;
import com.neg.technology.human.resource.Position.model.mapper.PositionMapper;
import com.neg.technology.human.resource.Position.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.Position.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.Position.model.response.PositionResponse;
import com.neg.technology.human.resource.Position.repository.PositionRepository;
import com.neg.technology.human.resource.Position.validator.PositionValidator;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.SalaryRequest;
import com.neg.technology.human.resource.Utility.request.TitleRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    // Entity-returning or Optional-returning eski metotlar durabilir, ama artık kullanma, controller bu yeni metotları çağıracak.

    @Override
    public List<PositionResponse> getAllPositions() {
        return positionRepository.findAll()
                .stream()
                .map(positionMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<Position> findByTitle(String title) {
        return Optional.empty();
    }

    @Override
    public boolean existsByTitle(String title) {
        return false;
    }

    @Override
    public List<Position> findByBaseSalaryGreaterThanEqual(BigDecimal salary) {
        return List.of();
    }

    @Override
    public Position save(Position position) {
        return null;
    }

    @Override
    public Optional<Position> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Position> findAll() {
        return List.of();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Position update(Long id, Position position) {
        return null;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public PositionResponse getPositionById(IdRequest request) {
        Position position = positionRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Position", request.getId()));
        return positionMapper.toDTO(position);
    }

    @Override
    public PositionResponse createPosition(CreatePositionRequest request) {
        positionValidator.validateCreate(request);
        Position position = positionMapper.toEntity(request);
        Position saved = positionRepository.save(position);
        BusinessLogger.logCreated(Position.class, saved.getId(), saved.getTitle());
        return positionMapper.toDTO(saved);
    }

    @Override
    public PositionResponse updatePosition(UpdatePositionRequest request) {
        if (!positionRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("Position", request.getId());
        }
        positionValidator.validateUpdate(request, request.getId());
        Position existing = positionRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Position", request.getId()));
        positionMapper.updateEntity(existing, request);
        Position updated = positionRepository.save(existing);
        BusinessLogger.logUpdated(Position.class, updated.getId(), updated.getTitle());
        return positionMapper.toDTO(updated);
    }

    @Override
    public void deletePosition(IdRequest request) {
        if (!positionRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("Position", request.getId());
        }
        positionRepository.deleteById(request.getId());
        BusinessLogger.logDeleted(Position.class, request.getId());
    }

    @Override
    public PositionResponse getPositionByTitle(TitleRequest request) {
        Position position = positionRepository.findByTitle(request.getTitle())
                .orElseThrow(() -> new ResourceNotFoundException("Position", request.getTitle()));
        return positionMapper.toDTO(position);
    }

    @Override
    public boolean existsByTitle(TitleRequest request) {
        return positionRepository.existsByTitle(request.getTitle());
    }

    @Override
    public List<PositionResponse> getPositionsByBaseSalary(SalaryRequest request) {
        return positionRepository.findByBaseSalaryGreaterThanEqual(request.getSalary())
                .stream()
                .map(positionMapper::toDTO)
                .toList();
    }
}
