package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.business.PositionValidator;
import com.neg.hr.human.resource.dto.CreatePositionDTO;
import com.neg.hr.human.resource.dto.PositionDTO;
import com.neg.hr.human.resource.dto.UpdatePositionDTO;
import com.neg.hr.human.resource.entity.Position;
import com.neg.hr.human.resource.mapper.PositionMapper;
import com.neg.hr.human.resource.service.PositionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;
    private final PositionValidator positionValidator;

    public PositionController(PositionService positionService, PositionValidator positionValidator) {
        this.positionService = positionService;
        this.positionValidator = positionValidator;
    }

    @GetMapping
    public List<PositionDTO> getAllPositions() {
        return positionService.findAll()
                .stream()
                .map(PositionMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDTO> getPositionById(@PathVariable Long id) {
        Optional<Position> opt = positionService.findById(id);
        return opt.map(position -> ResponseEntity.ok(PositionMapper.toDTO(position)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PositionDTO> createPosition(@Valid @RequestBody CreatePositionDTO dto) {
        positionValidator.validateCreate(dto);
        Position position = PositionMapper.toEntity(dto);
        Position saved = positionService.save(position);
        return ResponseEntity.ok(PositionMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionDTO> updatePosition(@PathVariable Long id, @Valid @RequestBody UpdatePositionDTO dto) {
        Optional<Position> existingOpt = positionService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        positionValidator.validateUpdate(dto, id);
        Position existing = existingOpt.get();
        PositionMapper.updateEntity(existing, dto);
        Position updated = positionService.save(existing);
        return ResponseEntity.ok(PositionMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        Optional<Position> existingOpt = positionService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        positionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Additional GETs

    @GetMapping("/title/{title}")
    public ResponseEntity<PositionDTO> getPositionByTitle(@PathVariable String title) {
        Optional<Position> opt = positionService.findByTitle(title);
        return opt.map(position -> ResponseEntity.ok(PositionMapper.toDTO(position)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/exists/{title}")
    public boolean existsByTitle(@PathVariable String title) {
        return positionService.existsByTitle(title);
    }

    @GetMapping("/salary/{salary}")
    public List<PositionDTO> getPositionsByBaseSalaryGreaterThanEqual(@PathVariable BigDecimal salary) {
        return positionService.findByBaseSalaryGreaterThanEqual(salary)
                .stream()
                .map(PositionMapper::toDTO)
                .toList();
    }
}
