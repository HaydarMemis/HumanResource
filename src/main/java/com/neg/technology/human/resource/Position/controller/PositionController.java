package com.neg.technology.human.resource.Position.controller;

import com.neg.technology.human.resource.Position.model.response.PositionResponseList;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.SalaryRequest;
import com.neg.technology.human.resource.Utility.request.TitleRequest;
import com.neg.technology.human.resource.Position.model.request.CreatePositionRequest;
import com.neg.technology.human.resource.Position.model.request.UpdatePositionRequest;
import com.neg.technology.human.resource.Position.model.response.PositionResponse;
import com.neg.technology.human.resource.Position.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @Operation(summary = "Get all positions", description = "Retrieves a list of all positions in the system.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @PostMapping("/getAll")
    public ResponseEntity<PositionResponseList> getAllPositions() {
        return ResponseEntity.ok((PositionResponseList) positionService.getAllPositions());
    }

    @Operation(summary = "Get position by ID", description = "Retrieves the position with the specified ID.")
    @ApiResponse(responseCode = "200", description = "Position found")
    @PostMapping("/getById")
    public ResponseEntity<PositionResponse> getPositionById(@Valid @RequestBody IdRequest request) {
        return ResponseEntity.ok(positionService.getPositionById(request));
    }

    @Operation(summary = "Create new position", description = "Creates a new position record.")
    @ApiResponse(responseCode = "200", description = "Position successfully created")
    @PostMapping("/create")
    public ResponseEntity<PositionResponse> createPosition(@Valid @RequestBody CreatePositionRequest request) {
        return ResponseEntity.ok(positionService.createPosition(request));
    }

    @Operation(summary = "Update position", description = "Updates an existing position.")
    @ApiResponse(responseCode = "200", description = "Position successfully updated")
    @PostMapping("/update")
    public ResponseEntity<PositionResponse> updatePosition(@Valid @RequestBody UpdatePositionRequest request) {
        return ResponseEntity.ok(positionService.updatePosition(request));
    }

    @Operation(summary = "Delete position", description = "Deletes the position with the specified ID.")
    @ApiResponse(responseCode = "204", description = "Position successfully deleted")
    @PostMapping("/delete")
    public ResponseEntity<Void> deletePosition(@Valid @RequestBody IdRequest request) {
        positionService.deletePosition(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get position by title", description = "Retrieves the position with the specified title.")
    @ApiResponse(responseCode = "200", description = "Position found")
    @PostMapping("/getByTitle")
    public ResponseEntity<PositionResponse> getPositionByTitle(@Valid @RequestBody TitleRequest request) {
        return ResponseEntity.ok(positionService.getPositionByTitle(request));
    }

    @Operation(summary = "Check if position exists by title", description = "Checks whether a position with the given title exists.")
    @ApiResponse(responseCode = "200", description = "Boolean result indicating existence")
    @PostMapping("/existsByTitle")
    public ResponseEntity<Boolean> existsByTitle(@Valid @RequestBody TitleRequest request) {
        return ResponseEntity.ok(positionService.existsByTitle(request));
    }

    @Operation(summary = "Get positions by base salary", description = "Retrieves all positions with a base salary greater than or equal to the specified amount.")
    @ApiResponse(responseCode = "200", description = "List of matching positions")
    @PostMapping("/getByBaseSalary")
    public ResponseEntity<PositionResponseList> getPositionsByBaseSalary(@Valid @RequestBody SalaryRequest request) {
        PositionResponseList responseList = positionService.getPositionsByBaseSalary(request);
        return ResponseEntity.ok(responseList);
    }

}
