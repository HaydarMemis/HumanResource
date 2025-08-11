package com.neg.technology.human.resource.Position.controller;

import com.neg.technology.human.resource.dto.utilities.IdRequest;
import com.neg.technology.human.resource.dto.utilities.SalaryRequest;
import com.neg.technology.human.resource.dto.utilities.TitleRequest;
import com.neg.technology.human.resource.dto.create.CreatePositionRequestDTO;
import com.neg.technology.human.resource.dto.entity.PositionEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdatePositionRequestDTO;
import com.neg.technology.human.resource.Position.model.entity.Position;
import com.neg.technology.human.resource.mapper.PositionMapper;
import com.neg.technology.human.resource.Position.service.PositionService;
import com.neg.technology.human.resource.Position.validator.PositionValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Get all positions", description = "Retrieves a list of all positions in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @PostMapping("/getAll")
    public List<PositionEntityDTO> getAllPositions() {
        return positionService.findAll()
                .stream()
                .map(PositionMapper::toDTO)
                .toList();
    }

    @Operation(summary = "Get position by ID", description = "Retrieves the position with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position found",
                    content = @Content(schema = @Schema(implementation = PositionEntityDTO.class))),
            @ApiResponse(responseCode = "404", description = "Position not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<PositionEntityDTO> getPositionById(
            @Parameter(description = "ID of the position") @Valid @RequestBody IdRequest request) {
        return positionService.findById(request.getId())
                .map(position -> ResponseEntity.ok(PositionMapper.toDTO(position)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new position", description = "Creates a new position record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position successfully created",
                    content = @Content(schema = @Schema(implementation = PositionEntityDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/create")
    public ResponseEntity<PositionEntityDTO> createPosition(
            @Parameter(description = "Details of the position to be created") @Valid @RequestBody CreatePositionRequestDTO dto) {
        positionValidator.validateCreate(dto);
        Position position = PositionMapper.toEntity(dto);
        Position saved = positionService.save(position);
        return ResponseEntity.ok(PositionMapper.toDTO(saved));
    }

    @Operation(summary = "Update position", description = "Updates an existing position.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position successfully updated",
                    content = @Content(schema = @Schema(implementation = PositionEntityDTO.class))),
            @ApiResponse(responseCode = "404", description = "Position not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/update")
    public ResponseEntity<PositionEntityDTO> updatePosition(
            @Parameter(description = "Details of the position to be updated") @Valid @RequestBody UpdatePositionRequestDTO dto) {
        if (!positionService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        positionValidator.validateUpdate(dto, dto.getId());
        Position existing = positionService.findById(dto.getId()).get();
        PositionMapper.updateEntity(existing, dto);
        Position updated = positionService.save(existing);
        return ResponseEntity.ok(PositionMapper.toDTO(updated));
    }

    @Operation(summary = "Delete position", description = "Deletes the position with the specified ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Position successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Position not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deletePosition(
            @Parameter(description = "ID of the position to delete") @Valid @RequestBody IdRequest request) {
        if (!positionService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        positionService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get position by title", description = "Retrieves the position with the specified title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position found",
                    content = @Content(schema = @Schema(implementation = PositionEntityDTO.class))),
            @ApiResponse(responseCode = "404", description = "Position not found")
    })
    @PostMapping("/getByTitle")
    public ResponseEntity<PositionEntityDTO> getPositionByTitle(
            @Parameter(description = "Title of the position") @Valid @RequestBody TitleRequest request) {
        return positionService.findByTitle(request.getTitle())
                .map(position -> ResponseEntity.ok(PositionMapper.toDTO(position)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Check if position exists by title", description = "Checks whether a position with the given title exists.")
    @ApiResponse(responseCode = "200", description = "Boolean result indicating existence")
    @PostMapping("/existsByTitle")
    public boolean existsByTitle(
            @Parameter(description = "Title of the position") @Valid @RequestBody TitleRequest request) {
        return positionService.existsByTitle(request.getTitle());
    }

    @Operation(summary = "Get positions by base salary", description = "Retrieves all positions with a base salary greater than or equal to the specified amount.")
    @ApiResponse(responseCode = "200", description = "List of matching positions")
    @PostMapping("/getByBaseSalary")
    public List<PositionEntityDTO> getPositionsByBaseSalary(
            @Parameter(description = "Minimum base salary filter") @Valid @RequestBody SalaryRequest request) {
        return positionService.findByBaseSalaryGreaterThanEqual(request.getSalary())
                .stream()
                .map(PositionMapper::toDTO)
                .toList();
    }
}
