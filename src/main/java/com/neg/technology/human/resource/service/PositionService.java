package com.neg.technology.human.resource.service;

import com.neg.technology.human.resource.entity.Position;

import java.util.List;
import java.util.Optional;

public interface PositionService {
    Optional<Position> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Position> findByBaseSalaryGreaterThanEqual(java.math.BigDecimal salary);

    Position save(Position position);

    Optional<Position> findById(Long id);

    List<Position> findAll();

    void deleteById(Long id);

    Position update(Long id, Position position);

    boolean existsById(Long id);
}
