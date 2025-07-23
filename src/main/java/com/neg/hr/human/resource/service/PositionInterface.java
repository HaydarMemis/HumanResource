package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Position;

import java.util.List;
import java.util.Optional;

public interface PositionInterface {
    Optional<Position> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Position> findByBaseSalaryGreaterThanEqual(java.math.BigDecimal salary);

    public Position save(Position position);

    public Optional<Position> findById(Long id);

    public List<Position> findAll();

    void deleteById(Long id);

    public Position update(Long id, Position position);
}
