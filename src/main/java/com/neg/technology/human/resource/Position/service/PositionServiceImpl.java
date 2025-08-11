package com.neg.technology.human.resource.Position.service;

import com.neg.technology.human.resource.business.BusinessLogger;
import com.neg.technology.human.resource.Position.model.entity.Position;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Position.repository.PositionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    public PositionServiceImpl(PositionRepository positionRepository) {
        this.positionRepository = positionRepository;
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
}
