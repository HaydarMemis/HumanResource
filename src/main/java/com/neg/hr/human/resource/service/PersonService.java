package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Person;
import com.neg.hr.human.resource.exception.ResourceNotFoundException;
import com.neg.hr.human.resource.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService implements PersonInterface {

    private final PersonRepository personRepository;

    // Constructor injection
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> findByGenderIgnoreCase(String gender) {
        return personRepository.findByGenderIgnoreCase(gender);
    }

    @Override
    public List<Person> findByBirthDateBefore(LocalDate birthDate) {
        return personRepository.findByBirthDateBefore(birthDate);
    }

    @Override
    public List<Person> findByMaritalStatusIgnoreCase(String maritalStatus) {
        return personRepository.findByMaritalStatusIgnoreCase(maritalStatus);
    }

    @Override
    public Optional<Person> findByNationalId(String nationalId) {
        return personRepository.findByNationalId(nationalId);
    }

    @Override
    public List<Person> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName) {
        return personRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
    }

    @Override
    public Optional<Person> findByEmailIgnoreCase(String email) {
        return personRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return personRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return personRepository.existsByNationalId(nationalId);
    }

    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    @Override
    public Optional<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Person", id);
        }
        personRepository.deleteById(id);
    }

    @Override
    public Person update(Long id, Person person) {
        Person existing = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person", id));

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
