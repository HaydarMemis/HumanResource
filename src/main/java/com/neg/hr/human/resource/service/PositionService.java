package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Position;
import com.neg.hr.human.resource.exception.ResourceNotFoundException;
import com.neg.hr.human.resource.repository.PositionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PositionService implements PositionInterface{
    private final PositionRepository positionRepository;

    public PositionService(PositionRepository positionRepository) {
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
        return positionRepository.save(position);
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
        if(!positionRepository.existsById(id)){
            throw new ResourceNotFoundException("Position",id);
        }
        positionRepository.deleteById(id);
    }

    @Override
    public Position update(Long id, Position position) {
        Position existing = positionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Position",id));
        existing.setTitle(position.getTitle());
        existing.setBaseSalary(position.getBaseSalary());

        return positionRepository.save(existing);
    }
}
