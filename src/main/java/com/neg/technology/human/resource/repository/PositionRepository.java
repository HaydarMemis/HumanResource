package com.neg.technology.human.resource.repository;

import com.neg.technology.human.resource.entity.Position;
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
