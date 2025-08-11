package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.*;
import com.neg.hr.human.resource.dto.create.CreatePersonRequestDTO;
import com.neg.hr.human.resource.dto.entity.PersonEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdatePersonRequestDTO;
import com.neg.hr.human.resource.entity.Person;
import com.neg.hr.human.resource.mapper.PersonMapper;
import com.neg.hr.human.resource.service.PersonService;
import com.neg.hr.human.resource.validator.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

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

    @PostMapping("/getAll")
    public List<PersonEntityDTO> getAllPersons() {
        return personService.findAll()
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
    }

    @PostMapping("/getById")
    public ResponseEntity<PersonEntityDTO> getPersonById(@Valid @RequestBody IdRequest request) {
        return personService.findById(request.getId())
                .map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<PersonEntityDTO> createPerson(@Valid @RequestBody CreatePersonRequestDTO dto) {
        personValidator.validateCreate(dto);
        Person person = PersonMapper.toEntity(dto);
        Person saved = personService.save(person);
        return ResponseEntity.ok(PersonMapper.toDTO(saved));
    }

    @PostMapping("/update")
    public ResponseEntity<PersonEntityDTO> updatePerson(@Valid @RequestBody UpdatePersonRequestDTO dto) {
        if (!personService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        personValidator.validateUpdate(dto, dto.getId());
        Person existingPerson = personService.findById(dto.getId()).get();
        PersonMapper.updateEntity(existingPerson, dto);
        Person updated = personService.save(existingPerson);
        return ResponseEntity.ok(PersonMapper.toDTO(updated));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deletePerson(@Valid @RequestBody IdRequest request) {
        if (!personService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        personService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/getByGender")
    public List<PersonEntityDTO> getPersonsByGender(@Valid @RequestBody GenderRequest request) {
        return personService.findByGenderIgnoreCase(request.getGender())
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
    }

    @PostMapping("/getBornBefore")
    public List<PersonEntityDTO> getPersonsBornBefore(@Valid @RequestBody DateRequest request) {
        LocalDate birthDate = LocalDate.parse(request.getDate());
        return personService.findByBirthDateBefore(birthDate)
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
    }

    @PostMapping("/getByMaritalStatus")
    public List<PersonEntityDTO> getPersonsByMaritalStatus(@Valid @RequestBody MaritalStatusRequest request) {
        return personService.findByMaritalStatusIgnoreCase(request.getStatus())
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
    }

    @PostMapping("/getByNationalId")
    public ResponseEntity<PersonEntityDTO> getPersonByNationalId(@Valid @RequestBody NationalIdRequest request) {
        return personService.findByNationalId(request.getNationalId())
                .map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/searchByName")
    public List<PersonEntityDTO> searchPersonsByName(@Valid @RequestBody NameSearchRequest request) {
        return personService.searchByOptionalNames(request.getFirstName(), request.getLastName())
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
    }

    @PostMapping("/getByEmail")
    public ResponseEntity<PersonEntityDTO> getPersonByEmail(@Valid @RequestBody EmailRequest request) {
        return personService.findByEmailIgnoreCase(request.getEmail())
                .map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}