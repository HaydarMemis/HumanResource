package com.neg.technology.human.resource.person.controller;

import com.neg.technology.human.resource.person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.person.model.response.PersonResponse;
import com.neg.technology.human.resource.person.service.PersonService;
import com.neg.technology.human.resource.person.validator.PersonValidator;
import com.neg.technology.human.resource.utility.module.entity.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "Person Controller", description = "Operations related to person management")
@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PersonValidator personValidator;

    @Operation(summary = "Get all persons", description = "Retrieve a list of all persons")
    @ApiResponse(responseCode = "200", description = "List of persons retrieved successfully")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<List<PersonResponse>>> getAllPersons() {
        return personService.getAllPersons()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get person by ID", description = "Retrieve a person by its unique ID")
    @ApiResponse(responseCode = "200", description = "Person found")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<PersonResponse>> getPersonById(
            @Parameter(description = "ID of the person to be retrieved", required = true)
            @Valid @RequestBody IdRequest request) {
        return personService.getPersonById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Create new person", description = "Create a new person record")
    @ApiResponse(responseCode = "200", description = "Person created successfully")
    @PostMapping("/create")
    public Mono<ResponseEntity<PersonResponse>> createPerson(
            @Parameter(description = "Person data for creation", required = true)
            @Valid @RequestBody CreatePersonRequest dto) {
        return personValidator.validateCreate(dto)
                .then(personService.createPerson(dto))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update existing person", description = "Update details of an existing person")
    @ApiResponse(responseCode = "200", description = "Person updated successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/update")
    public Mono<ResponseEntity<PersonResponse>> updatePerson(
            @Parameter(description = "Person data for update", required = true)
            @Valid @RequestBody UpdatePersonRequest dto) {
        return personValidator.validateUpdate(dto, dto.getId())
                .then(personService.updatePerson(dto))
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete person", description = "Delete a person by ID")
    @ApiResponse(responseCode = "204", description = "Person deleted successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deletePerson(
            @Parameter(description = "ID of the person to be deleted", required = true)
            @Valid @RequestBody IdRequest request) {
        return personService.deletePerson(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get persons by gender", description = "Retrieve a list of persons filtered by gender")
    @ApiResponse(responseCode = "200", description = "List of persons filtered by gender retrieved successfully")
    @PostMapping("/getByGender")
    public Mono<ResponseEntity<List<PersonResponse>>> getPersonsByGender(
            @Parameter(description = "Gender to filter by", required = true)
            @Valid @RequestBody GenderRequest request) {
        return personService.getPersonsByGender(request.getGender())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get persons born before a date", description = "Retrieve persons born before the specified date")
    @ApiResponse(responseCode = "200", description = "List of persons born before given date retrieved successfully")
    @PostMapping("/getBornBefore")
    public Mono<ResponseEntity<List<PersonResponse>>> getPersonsBornBefore(
            @Parameter(description = "Date to compare birth dates (yyyy-MM-dd)", required = true)
            @Valid @RequestBody DateRequest request) {
        return personService.getPersonsBornBefore(request.getDate())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get persons by marital status", description = "Retrieve a list of persons filtered by marital status")
    @ApiResponse(responseCode = "200", description = "List of persons filtered by marital status retrieved successfully")
    @PostMapping("/getByMaritalStatus")
    public Mono<ResponseEntity<List<PersonResponse>>> getPersonsByMaritalStatus(
            @Parameter(description = "Marital status to filter by", required = true)
            @Valid @RequestBody MaritalStatusRequest request) {
        return personService.getPersonsByMaritalStatus(request.getStatus())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get person by national ID", description = "Retrieve a person by their national ID")
    @ApiResponse(responseCode = "200", description = "Person found")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/getByNationalId")
    public Mono<ResponseEntity<PersonResponse>> getPersonByNationalId(
            @Parameter(description = "National ID of the person", required = true)
            @Valid @RequestBody NationalIdRequest request) {
        return personService.getPersonByNationalId(request.getNationalId())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Search persons by name", description = "Search for persons by first and/or last name")
    @ApiResponse(responseCode = "200", description = "List of persons matching the search criteria retrieved successfully")
    @PostMapping("/searchByName")
    public Mono<ResponseEntity<List<PersonResponse>>> searchPersonsByName(
            @Parameter(description = "Search criteria containing first and/or last name", required = true)
            @Valid @RequestBody NameSearchRequest request) {
        return personService.searchPersonsByName(request.getFirstName(), request.getLastName())
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get person by email", description = "Retrieve a person by their email address")
    @ApiResponse(responseCode = "200", description = "Person found")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/getByEmail")
    public Mono<ResponseEntity<PersonResponse>> getPersonByEmail(
            @Parameter(description = "Email of the person", required = true)
            @Valid @RequestBody EmailRequest request) {
        return personService.getPersonByEmail(request.getEmail())
                .map(ResponseEntity::ok);
    }
}