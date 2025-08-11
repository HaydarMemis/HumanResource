package com.neg.technology.human.resource.Position.repository;

import com.neg.technology.human.resource.Position.model.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    Optional<Position> findByTitle(String title);

    boolean existsByTitle(String title);

    List<Position> findByBaseSalaryGreaterThanEqual(java.math.BigDecimal salary);
}
