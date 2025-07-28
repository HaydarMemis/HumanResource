package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.Position;
import com.neg.hr.human.resource.service.impl.PositionServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionServiceImpl positionService;

    // Constructor injection
    public PositionController(PositionServiceImpl positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public List<Position> getAllPositions() {
        return positionService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Position> getPositionById(@PathVariable Long id) {
        Optional<Position> positionOpt = positionService.findById(id);
        return positionOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Position createPosition(@RequestBody Position position) {
        return positionService.save(position);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Position> updatePosition(@PathVariable Long id, @RequestBody Position position) {
        Optional<Position> existingPosition = positionService.findById(id);
        if (!existingPosition.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Position updated = positionService.update(id, position);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        Optional<Position> existingPosition = positionService.findById(id);
        if (!existingPosition.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        positionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Find by title
    @GetMapping("/title/{title}")
    public ResponseEntity<Position> getPositionByTitle(@PathVariable String title) {
        Optional<Position> positionOpt = positionService.findByTitle(title);
        return positionOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Check if position exists by title
    @GetMapping("/exists/{title}")
    public boolean existsByTitle(@PathVariable String title) {
        return positionService.existsByTitle(title);
    }

    // Find positions with base salary >= provided salary
    @GetMapping("/salary/{salary}")
    public List<Position> getPositionsByBaseSalaryGreaterThanEqual(@PathVariable BigDecimal salary) {
        return positionService.findByBaseSalaryGreaterThanEqual(salary);
    }
}
