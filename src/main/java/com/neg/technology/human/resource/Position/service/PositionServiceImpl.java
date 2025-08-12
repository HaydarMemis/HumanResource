package com.neg.technology.human.resource.Position.service;

import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Position.model.entity.Position;
import com.neg.technology.human.resource.Position.model.mapper.PositionMapper;
import com.neg.technology.human.resource.Position.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.Position.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.Position.model.response.PositionResponse;
import com.neg.technology.human.resource.Position.model.response.PositionResponseList;
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


    @Override
    public List<PositionResponse> getAllPositions() {
        List<PositionResponse> list = positionRepository.findAll()
                .stream()
                .map(positionMapper::toDTO)
                .toList();
        return (List<PositionResponse>) new PositionResponseList(list);
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
        BusinessLogger.logCreated(Position.class, saved.getId(), saved.getTitle());
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
            throw new ResourceNotFoundException("Position", id);
        }
        positionRepository.deleteById(id);
        BusinessLogger.logDeleted(Position.class, id);
    }

    @Override
    public Position update(Long id, Position position) {
        Position existing = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position", id));
        existing.setTitle(position.getTitle());
        existing.setBaseSalary(position.getBaseSalary());

        Position updated = positionRepository.save(existing);
        BusinessLogger.logUpdated(Position.class, updated.getId(), updated.getTitle());
        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return positionRepository.existsById(id);
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
    public PositionResponseList getPositionsByBaseSalary(SalaryRequest request) {
        List<PositionResponse> list = positionRepository.findByBaseSalaryGreaterThanEqual(request.getSalary())
                .stream()
                .map(positionMapper::toDTO)
                .toList();
        return new PositionResponseList(list);
    }
}
