package com.neg.hr.human.resouce.controller;

import com.neg.hr.human.resouce.entity.Person;
import com.neg.hr.human.resouce.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    // Constructor injection
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    // Get all persons
    @GetMapping
    public List<Person> getAllPersons() {
        return personService.findAll();
    }

    // Get person by ID
    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        try {
            Person person = personService.findById(id);
            return ResponseEntity.ok(person);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Create a new person
    @PostMapping
    public Person createPerson(@RequestBody Person person) {
        return personService.save(person);
    }

    // Update person
    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person person) {
        try {
            Person updated = personService.update(id, person);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete person
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        try {
            personService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get persons by gender
    @GetMapping("/gender/{gender}")
    public List<Person> getPersonsByGender(@PathVariable String gender) {
        // You will need to implement findByGender in PersonService + PersonRepository
        throw new UnsupportedOperationException("findByGender not yet implemented.");
    }

    // Get persons born before a specific date
    @GetMapping("/born-before/{date}")
    public List<Person> getPersonsBornBefore(@PathVariable String date) {
        LocalDate birthDate = LocalDate.parse(date);
        // You will need to implement findByBirthDateBefore in PersonService + PersonRepository
        throw new UnsupportedOperationException("findByBirthDateBefore not yet implemented.");
    }

    // Get persons by marital status
    @GetMapping("/marital-status/{status}")
    public List<Person> getPersonsByMaritalStatus(@PathVariable String status) {
        // You will need to implement findByMaritalStatus in PersonService + PersonRepository
        throw new UnsupportedOperationException("findByMaritalStatus not yet implemented.");
    }
}
