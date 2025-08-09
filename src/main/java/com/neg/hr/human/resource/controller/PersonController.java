package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.validator.PersonValidator;
import com.neg.hr.human.resource.dto.create.CreatePersonRequestDTO;
import com.neg.hr.human.resource.dto.entity.PersonEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdatePersonRequestDTO;
import com.neg.hr.human.resource.entity.Person;
import com.neg.hr.human.resource.mapper.PersonMapper;
import com.neg.hr.human.resource.service.impl.PersonServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonServiceImpl personService;
    private final PersonValidator personValidator;

    public PersonController(PersonServiceImpl personService, PersonValidator personValidator) {
        this.personService = personService;
        this.personValidator = personValidator;
    }

    @GetMapping
    public List<PersonEntityDTO> getAllPersons() {
        return personService.findAll()
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonEntityDTO> getPersonById(@PathVariable Long id) {
        Optional<Person> personOpt = personService.findById(id);
        return personOpt.map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonEntityDTO> createPerson(@Valid @RequestBody CreatePersonRequestDTO dto) {
        personValidator.validateCreate(dto);
        Person person = PersonMapper.toEntity(dto);
        Person saved = personService.save(person);
        return ResponseEntity.ok(PersonMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonEntityDTO> updatePerson(@PathVariable Long id, @Valid @RequestBody UpdatePersonRequestDTO dto) {
        Optional<Person> existingPersonOpt = personService.findById(id);
        if (existingPersonOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        personValidator.validateUpdate(dto, id);
        Person existingPerson = existingPersonOpt.get();
        PersonMapper.updateEntity(existingPerson, dto);
        Person updated = personService.save(existingPerson);
        return ResponseEntity.ok(PersonMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        Optional<Person> existingPerson = personService.findById(id);
        if (existingPerson.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Additional filtered GETs similar to your current controller:

    @GetMapping("/gender/{gender}")
    public List<PersonEntityDTO> getPersonsByGender(@PathVariable String gender) {
        return personService.findByGenderIgnoreCase(gender)
                .stream().map(PersonMapper::toDTO)
                .toList();
    }

    @GetMapping("/born-before/{date}")
    public List<PersonEntityDTO> getPersonsBornBefore(@PathVariable String date) {
        LocalDate birthDate = LocalDate.parse(date);
        return personService.findByBirthDateBefore(birthDate)
                .stream().map(PersonMapper::toDTO)
                .toList();
    }

    @GetMapping("/marital-status/{status}")
    public List<PersonEntityDTO> getPersonsByMaritalStatus(@PathVariable String status) {
        return personService.findByMaritalStatusIgnoreCase(status)
                .stream().map(PersonMapper::toDTO)
                .toList();
    }

    @GetMapping("/national-id/{nationalId}")
    public ResponseEntity<PersonEntityDTO> getPersonByNationalId(@PathVariable String nationalId) {
        Optional<Person> personOpt = personService.findByNationalId(nationalId);
        return personOpt.map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<PersonEntityDTO> searchPersonsByName(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {

        return personService.searchByOptionalNames(firstName, lastName)
                .stream().map(PersonMapper::toDTO)
                .toList();
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<PersonEntityDTO> getPersonByEmail(@PathVariable String email) {
        Optional<Person> personOpt = personService.findByEmailIgnoreCase(email);
        return personOpt.map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
