package com.neg.technology.human.resource.Person.controller;

import com.neg.technology.human.resource.Person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.Person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.Person.model.response.PersonResponse;
import com.neg.technology.human.resource.Utility.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Person Controller", description = "Operations related to person management")
@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final com.neg.technology.human.resource.Person.service.PersonService personService;

    public PersonController(com.neg.technology.human.resource.Person.service.PersonService personService) {
        this.personService = personService;
    }

    @Operation(summary = "Get all persons", description = "Retrieve a list of all persons")
    @ApiResponse(responseCode = "200", description = "List of persons retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<PersonResponse>> getAllPersons() {
        return personService.getAllPersons();
    }

    @Operation(summary = "Get person by ID", description = "Retrieve a person by its unique ID")
    @ApiResponse(responseCode = "200", description = "Person found")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/getById")
    public ResponseEntity<PersonResponse> getPersonById(
            @Parameter(description = "ID of the person to be retrieved", required = true)
            @Valid @RequestBody IdRequest request) {
        return personService.getPersonById(request);
    }

    @Operation(summary = "Create new person", description = "Create a new person record")
    @ApiResponse(responseCode = "200", description = "Person created successfully")
    @PostMapping("/create")
    public ResponseEntity<PersonResponse> createPerson(
            @Parameter(description = "Person data for creation", required = true)
            @Valid @RequestBody CreatePersonRequest dto) {
        return personService.createPerson(dto);
    }

    @Operation(summary = "Update existing person", description = "Update details of an existing person")
    @ApiResponse(responseCode = "200", description = "Person updated successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/update")
    public ResponseEntity<PersonResponse> updatePerson(
            @Parameter(description = "Person data for update", required = true)
            @Valid @RequestBody UpdatePersonRequest dto) {
        return personService.updatePerson(dto);
    }

    @Operation(summary = "Delete person", description = "Delete a person by ID")
    @ApiResponse(responseCode = "204", description = "Person deleted successfully")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/delete")
    public ResponseEntity<Void> deletePerson(
            @Parameter(description = "ID of the person to be deleted", required = true)
            @Valid @RequestBody IdRequest request) {
        return personService.deletePerson(request);
    }

    @Operation(summary = "Get persons by gender", description = "Retrieve a list of persons filtered by gender")
    @ApiResponse(responseCode = "200", description = "List of persons filtered by gender retrieved successfully")
    @PostMapping("/getByGender")
    public ResponseEntity<List<PersonResponse>> getPersonsByGender(
            @Parameter(description = "Gender to filter by", required = true)
            @Valid @RequestBody GenderRequest request) {
        // Burada service'de yeni method yazmalısın, örnek:
        return personService.getPersonsByGender(request.getGender());
    }

    @Operation(summary = "Get persons born before a date", description = "Retrieve persons born before the specified date")
    @ApiResponse(responseCode = "200", description = "List of persons born before given date retrieved successfully")
    @PostMapping("/getBornBefore")
    public ResponseEntity<List<PersonResponse>> getPersonsBornBefore(
            @Parameter(description = "Date to compare birth dates (yyyy-MM-dd)", required = true)
            @Valid @RequestBody DateRequest request) {
        return personService.getPersonsBornBefore(request.getDate());
    }

    @Operation(summary = "Get persons by marital status", description = "Retrieve a list of persons filtered by marital status")
    @ApiResponse(responseCode = "200", description = "List of persons filtered by marital status retrieved successfully")
    @PostMapping("/getByMaritalStatus")
    public ResponseEntity<List<PersonResponse>> getPersonsByMaritalStatus(
            @Parameter(description = "Marital status to filter by", required = true)
            @Valid @RequestBody MaritalStatusRequest request) {
        return personService.getPersonsByMaritalStatus(request.getStatus());
    }

    @Operation(summary = "Get person by national ID", description = "Retrieve a person by their national ID")
    @ApiResponse(responseCode = "200", description = "Person found")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/getByNationalId")
    public ResponseEntity<PersonResponse> getPersonByNationalId(
            @Parameter(description = "National ID of the person", required = true)
            @Valid @RequestBody NationalIdRequest request) {
        return personService.getPersonByNationalId(request.getNationalId());
    }

    @Operation(summary = "Search persons by name", description = "Search for persons by first and/or last name")
    @ApiResponse(responseCode = "200", description = "List of persons matching the search criteria retrieved successfully")
    @PostMapping("/searchByName")
    public ResponseEntity<List<PersonResponse>> searchPersonsByName(
            @Parameter(description = "Search criteria containing first and/or last name", required = true)
            @Valid @RequestBody NameSearchRequest request) {
        return personService.searchPersonsByName(request.getFirstName(), request.getLastName());
    }

    @Operation(summary = "Get person by email", description = "Retrieve a person by their email address")
    @ApiResponse(responseCode = "200", description = "Person found")
    @ApiResponse(responseCode = "404", description = "Person not found")
    @PostMapping("/getByEmail")
    public ResponseEntity<PersonResponse> getPersonByEmail(
            @Parameter(description = "Email of the person", required = true)
            @Valid @RequestBody EmailRequest request) {
        return personService.getPersonByEmail(request.getEmail());
    }
}
