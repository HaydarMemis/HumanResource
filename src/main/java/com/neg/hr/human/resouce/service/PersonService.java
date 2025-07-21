package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Person;
import com.neg.hr.human.resouce.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    // Save a new person
    public Person save(Person person) {
        return personRepository.save(person);
    }

    // Find a person by ID
    public Person findById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id " + id));
    }

    // Get all persons
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    // Delete person by ID
    public void deleteById(Long id) {
        if (!personRepository.existsById(id)) {
            throw new EntityNotFoundException("Person not found with id " + id);
        }
        personRepository.deleteById(id);
    }

    // Update person
    public Person update(Long id, Person person) {
        Person existing = findById(id);
        existing.setFirstName(person.getFirstName());
        existing.setLastName(person.getLastName());
        existing.setNationalId(person.getNationalId());
        existing.setBirthDate(person.getBirthDate());
        existing.setGender(person.getGender());
        existing.setEmail(person.getEmail());
        existing.setPhone(person.getPhone());
        existing.setAddress(person.getAddress());
        existing.setMaritalStatus(person.getMaritalStatus());
        return personRepository.save(existing);
    }
}
