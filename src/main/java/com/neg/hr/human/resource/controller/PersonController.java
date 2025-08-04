package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.business.PersonValidator;
import com.neg.hr.human.resource.dto.create.CreatePersonDTO;
import com.neg.hr.human.resource.dto.PersonDTO;
import com.neg.hr.human.resource.dto.update.UpdatePersonDTO;
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
    public List<PersonDTO> getAllPersons() {
        return personService.findAll()
                .stream()
                .map(PersonMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        Optional<Person> personOpt = personService.findById(id);
        return personOpt.map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PersonDTO> createPerson(@Valid @RequestBody CreatePersonDTO dto) {
        personValidator.validateCreate(dto);
        Person person = PersonMapper.toEntity(dto);
        Person saved = personService.save(person);
        return ResponseEntity.ok(PersonMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable Long id, @Valid @RequestBody UpdatePersonDTO dto) {
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
    public List<PersonDTO> getPersonsByGender(@PathVariable String gender) {
        return personService.findByGenderIgnoreCase(gender)
                .stream().map(PersonMapper::toDTO)
                .toList();
    }

    @GetMapping("/born-before/{date}")
    public List<PersonDTO> getPersonsBornBefore(@PathVariable String date) {
        LocalDate birthDate = LocalDate.parse(date);
        return personService.findByBirthDateBefore(birthDate)
                .stream().map(PersonMapper::toDTO)
                .toList();
    }

    @GetMapping("/marital-status/{status}")
    public List<PersonDTO> getPersonsByMaritalStatus(@PathVariable String status) {
        return personService.findByMaritalStatusIgnoreCase(status)
                .stream().map(PersonMapper::toDTO)
                .toList();
    }

    @GetMapping("/national-id/{nationalId}")
    public ResponseEntity<PersonDTO> getPersonByNationalId(@PathVariable String nationalId) {
        Optional<Person> personOpt = personService.findByNationalId(nationalId);
        return personOpt.map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<PersonDTO> searchPersonsByName(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName) {

        return personService.searchByOptionalNames(firstName, lastName)
                .stream().map(PersonMapper::toDTO)
                .toList();
    }


    @GetMapping("/email/{email}")
    public ResponseEntity<PersonDTO> getPersonByEmail(@PathVariable String email) {
        Optional<Person> personOpt = personService.findByEmailIgnoreCase(email);
        return personOpt.map(person -> ResponseEntity.ok(PersonMapper.toDTO(person)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
