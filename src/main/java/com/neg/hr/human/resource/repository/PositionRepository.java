package com.neg.hr.human.resouce.repository;

import com.neg.hr.human.resouce.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Position> findByBaseSalaryGreaterThanEqual(java.math.BigDecimal salary);
}
