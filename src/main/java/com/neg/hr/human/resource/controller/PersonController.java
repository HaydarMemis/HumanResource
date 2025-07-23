package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.Person;
import com.neg.hr.human.resource.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    // Constructor injection (Spring automatically injects)
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Optional<Person> personOpt = personService.findById(id);
        return personOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Person createPerson(@RequestBody Person person) {
        return personService.save(person);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person person) {
        Optional<Person> existingPerson = personService.findById(id);
        if (!existingPerson.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        person.setId(id);
        Person updated = personService.save(person);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        Optional<Person> existingPerson = personService.findById(id);
        if (!existingPerson.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        personService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Get persons by gender
    @GetMapping("/gender/{gender}")
    public List<Person> getPersonsByGender(@PathVariable String gender) {
        return personService.findByGenderIgnoreCase(gender);
    }

    // Get persons born before a specific date
    @GetMapping("/born-before/{date}")
    public List<Person> getPersonsBornBefore(@PathVariable String date) {
        LocalDate birthDate = LocalDate.parse(date); // Expects "yyyy-MM-dd"
        return personService.findByBirthDateBefore(birthDate);
    }

    // Get persons by marital status
    @GetMapping("/marital-status/{status}")
    public List<Person> getPersonsByMaritalStatus(@PathVariable String status) {
        return personService.findByMaritalStatusIgnoreCase(status);
    }

    // Get person by national ID
    @GetMapping("/national-id/{nationalId}")
    public ResponseEntity<Person> getPersonByNationalId(@PathVariable String nationalId) {
        Optional<Person> personOpt = personService.findByNationalId(nationalId);
        return personOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Search persons by partial first and last name
    @GetMapping("/search")
    public List<Person> searchPersonsByName(
            @RequestParam String firstName,
            @RequestParam String lastName) {
        return personService.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
    }

    // Get person by email
    @GetMapping("/email/{email}")
    public ResponseEntity<Person> getPersonByEmail(@PathVariable String email) {
        Optional<Person> personOpt = personService.findByEmailIgnoreCase(email);
        return personOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
