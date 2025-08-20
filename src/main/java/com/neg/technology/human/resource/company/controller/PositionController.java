package com.neg.technology.human.resource.company.controller;

import com.neg.technology.human.resource.company.model.response.PositionResponseList;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.SalaryRequest;
import com.neg.technology.human.resource.utility.module.entity.request.TitleRequest;
import com.neg.technology.human.resource.company.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.company.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.company.model.response.PositionResponse;
import com.neg.technology.human.resource.company.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "Get all positions", description = "Retrieves a list of all positions in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<PositionResponseList>> getAllPositions() {
        return positionService.getAllPositions()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get position by ID", description = "Retrieves the position with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Position found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<PositionResponse>> getPositionById(@Valid @RequestBody IdRequest request) {
        return positionService.getPositionById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Create new position", description = "Creates a new position record.")
    @ApiResponse(responseCode = "200", description = "Position successfully created")
    @PostMapping("/create")
    public Mono<ResponseEntity<PositionResponse>> createPosition(@Valid @RequestBody CreatePositionRequest request) {
        return positionService.createPosition(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update position", description = "Updates an existing position.")
    @ApiResponse(responseCode = "200", description = "Position successfully updated")
    @PostMapping("/update")
    public Mono<ResponseEntity<PositionResponse>> updatePosition(@Valid @RequestBody UpdatePositionRequest request) {
        return positionService.updatePosition(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete position", description = "Deletes the position with the specified ID.")
    @ApiResponse(responseCode = "204", description = "Position successfully deleted")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deletePosition(@Valid @RequestBody IdRequest request) {
        return positionService.deletePosition(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get position by title", description = "Retrieves the position with the specified title.")
    @ApiResponse(responseCode = "200", description = "Position found")
    @PostMapping("/getByTitle")
    public Mono<ResponseEntity<PositionResponse>> getPositionByTitle(@Valid @RequestBody TitleRequest request) {
        return positionService.getPositionByTitle(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Check if position exists by title", description = "Checks whether a position with the given title exists.")
    @ApiResponse(responseCode = "200", description = "Boolean result indicating existence")
    @PostMapping("/existsByTitle")
    public Mono<ResponseEntity<Boolean>> existsByTitle(@Valid @RequestBody TitleRequest request) {
        return positionService.existsByTitle(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get positions by base salary", description = "Retrieves all positions with a base salary greater than or equal to the specified amount.")
    @ApiResponse(responseCode = "200", description = "List of matching positions")
    @PostMapping("/getByBaseSalary")
    public Mono<ResponseEntity<PositionResponseList>> getPositionsByBaseSalary(@Valid @RequestBody SalaryRequest request) {
        return positionService.getPositionsByBaseSalary(request)
                .map(ResponseEntity::ok);
    }
}