package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.IdRequest;
import com.neg.hr.human.resource.dto.SalaryRequest;
import com.neg.hr.human.resource.dto.TitleRequest;
import com.neg.hr.human.resource.dto.create.CreatePositionRequestDTO;
import com.neg.hr.human.resource.dto.entity.PositionEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdatePositionRequestDTO;
import com.neg.hr.human.resource.entity.Position;
import com.neg.hr.human.resource.mapper.PositionMapper;
import com.neg.hr.human.resource.service.PositionService;
import com.neg.hr.human.resource.validator.PositionValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;
    private final PositionValidator positionValidator;

    public PositionController(PositionService positionService,
                              PositionValidator positionValidator) {
        this.positionService = positionService;
        this.positionValidator = positionValidator;
    }

    @PostMapping("/getAll")
    public List<PositionEntityDTO> getAllPositions() {
        return positionService.findAll()
                .stream()
                .map(PositionMapper::toDTO)
                .toList();
    }

    @PostMapping("/getById")
    public ResponseEntity<PositionEntityDTO> getPositionById(@Valid @RequestBody IdRequest request) {
        return positionService.findById(request.getId())
                .map(position -> ResponseEntity.ok(PositionMapper.toDTO(position)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<PositionEntityDTO> createPosition(@Valid @RequestBody CreatePositionRequestDTO dto) {
        positionValidator.validateCreate(dto);
        Position position = PositionMapper.toEntity(dto);
        Position saved = positionService.save(position);
        return ResponseEntity.ok(PositionMapper.toDTO(saved));
    }

    @PostMapping("/update")
    public ResponseEntity<PositionEntityDTO> updatePosition(@Valid @RequestBody UpdatePositionRequestDTO dto) {
        if (!positionService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        positionValidator.validateUpdate(dto, dto.getId());
        Position existing = positionService.findById(dto.getId()).get();
        PositionMapper.updateEntity(existing, dto);
        Position updated = positionService.save(existing);
        return ResponseEntity.ok(PositionMapper.toDTO(updated));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deletePosition(@Valid @RequestBody IdRequest request) {
        if (!positionService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        positionService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/getByTitle")
    public ResponseEntity<PositionEntityDTO> getPositionByTitle(@Valid @RequestBody TitleRequest request) {
        return positionService.findByTitle(request.getTitle())
                .map(position -> ResponseEntity.ok(PositionMapper.toDTO(position)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/existsByTitle")
    public boolean existsByTitle(@Valid @RequestBody TitleRequest request) {
        return positionService.existsByTitle(request.getTitle());
    }

    @PostMapping("/getByBaseSalary")
    public List<PositionEntityDTO> getPositionsByBaseSalary(@Valid @RequestBody SalaryRequest request) {
        return positionService.findByBaseSalaryGreaterThanEqual(request.getSalary())
                .stream()
                .map(PositionMapper::toDTO)
                .toList();
    }
}