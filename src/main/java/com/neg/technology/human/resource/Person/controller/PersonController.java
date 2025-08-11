package com.neg.technology.human.resource.Person.controller;

import com.neg.technology.human.resource.dto.create.CreatePersonRequestDTO;
import com.neg.technology.human.resource.dto.entity.PersonEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdatePersonRequestDTO;
import com.neg.hr.human.resource.dto.utilities.*;
import com.neg.technology.human.resource.dto.utilities.*;
import com.neg.technology.human.resource.Person.model.entity.Person;
import com.neg.technology.human.resource.mapper.PersonMapper;
import com.neg.technology.human.resource.Person.service.PersonService;
import com.neg.technology.human.resource.Person.validator.PersonValidator;
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

    private final PersonService personService;
    private final PersonValidator personValidator;

    public PersonController(PersonService personService,
                            PersonValidator personValidator) {
        this.personService = personService;
        this.personValidator = personValidator;
    }

    @Operation(summary = "Get all persons", description = "Retrieve a list of all persons")
    @ApiResponse(responseCode = "200", description = "List of persons retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<PersonEntityDTO>> getAllPersons() {
        List<PersonEntityDTO> persons = personService.findAll()
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
        return ResponseEntity.ok(persons);
    }

    @Operation(summary = "Get person by ID", description = "Retrieve a person by its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<PersonEntityDTO> getPersonById(
            @Parameter(description = "ID of the person to be retrieved", required = true)
            @Valid @RequestBody IdRequest request) {
        return personService.findById(request.getId())
                .map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new person", description = "Create a new person record")
    @ApiResponse(responseCode = "200", description = "Person created successfully")
    @PostMapping("/create")
    public ResponseEntity<PersonEntityDTO> createPerson(
            @Parameter(description = "Person data for creation", required = true)
            @Valid @RequestBody CreatePersonRequestDTO dto) {
        personValidator.validateCreate(dto);
        Person person = PersonMapper.toEntity(dto);
        Person saved = personService.save(person);
        return ResponseEntity.ok(PersonMapper.toDTO(saved));
    }

    @Operation(summary = "Update existing person", description = "Update details of an existing person")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person updated successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    @PostMapping("/update")
    public ResponseEntity<PersonEntityDTO> updatePerson(
            @Parameter(description = "Person data for update", required = true)
            @Valid @RequestBody UpdatePersonRequestDTO dto) {
        if (!personService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        personValidator.validateUpdate(dto, dto.getId());
        Person existingPerson = personService.findById(dto.getId()).get();
        PersonMapper.updateEntity(existingPerson, dto);
        Person updated = personService.save(existingPerson);
        return ResponseEntity.ok(PersonMapper.toDTO(updated));
    }

    @Operation(summary = "Delete person", description = "Delete a person by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Person deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deletePerson(
            @Parameter(description = "ID of the person to be deleted", required = true)
            @Valid @RequestBody IdRequest request) {
        if (!personService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        personService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get persons by gender", description = "Retrieve a list of persons filtered by gender")
    @ApiResponse(responseCode = "200", description = "List of persons filtered by gender retrieved successfully")
    @PostMapping("/getByGender")
    public ResponseEntity<List<PersonEntityDTO>> getPersonsByGender(
            @Parameter(description = "Gender to filter by", required = true)
            @Valid @RequestBody GenderRequest request) {
        List<PersonEntityDTO> persons = personService.findByGenderIgnoreCase(request.getGender())
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
        return ResponseEntity.ok(persons);
    }

    @Operation(summary = "Get persons born before a date", description = "Retrieve persons born before the specified date")
    @ApiResponse(responseCode = "200", description = "List of persons born before given date retrieved successfully")
    @PostMapping("/getBornBefore")
    public ResponseEntity<List<PersonEntityDTO>> getPersonsBornBefore(
            @Parameter(description = "Date to compare birth dates (yyyy-MM-dd)", required = true)
            @Valid @RequestBody DateRequest request) {
        LocalDate birthDate = LocalDate.parse(request.getDate());
        List<PersonEntityDTO> persons = personService.findByBirthDateBefore(birthDate)
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
        return ResponseEntity.ok(persons);
    }

    @Operation(summary = "Get persons by marital status", description = "Retrieve a list of persons filtered by marital status")
    @ApiResponse(responseCode = "200", description = "List of persons filtered by marital status retrieved successfully")
    @PostMapping("/getByMaritalStatus")
    public ResponseEntity<List<PersonEntityDTO>> getPersonsByMaritalStatus(
            @Parameter(description = "Marital status to filter by", required = true)
            @Valid @RequestBody MaritalStatusRequest request) {
        List<PersonEntityDTO> persons = personService.findByMaritalStatusIgnoreCase(request.getStatus())
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
        return ResponseEntity.ok(persons);
    }

    @Operation(summary = "Get person by national ID", description = "Retrieve a person by their national ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    @PostMapping("/getByNationalId")
    public ResponseEntity<PersonEntityDTO> getPersonByNationalId(
            @Parameter(description = "National ID of the person", required = true)
            @Valid @RequestBody NationalIdRequest request) {
        return personService.findByNationalId(request.getNationalId())
                .map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Search persons by name", description = "Search for persons by first and/or last name")
    @ApiResponse(responseCode = "200", description = "List of persons matching the search criteria retrieved successfully")
    @PostMapping("/searchByName")
    public ResponseEntity<List<PersonEntityDTO>> searchPersonsByName(
            @Parameter(description = "Search criteria containing first and/or last name", required = true)
            @Valid @RequestBody NameSearchRequest request) {
        List<PersonEntityDTO> persons = personService.searchByOptionalNames(request.getFirstName(), request.getLastName())
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
        return ResponseEntity.ok(persons);
    }

    @Operation(summary = "Get person by email", description = "Retrieve a person by their email address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Person found"),
            @ApiResponse(responseCode = "404", description = "Person not found")
    })
    @PostMapping("/getByEmail")
    public ResponseEntity<PersonEntityDTO> getPersonByEmail(
            @Parameter(description = "Email of the person", required = true)
            @Valid @RequestBody EmailRequest request) {
        return personService.findByEmailIgnoreCase(request.getEmail())
                .map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
