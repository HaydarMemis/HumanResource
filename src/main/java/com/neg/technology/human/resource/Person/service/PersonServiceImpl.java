package com.neg.technology.human.resource.Person.service;

import com.neg.technology.human.resource.Person.model.entity.Person;
import com.neg.technology.human.resource.Person.model.mapper.PersonMapper;
import com.neg.technology.human.resource.Person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.Person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.Person.model.response.PersonResponse;
import com.neg.technology.human.resource.Person.repository.PersonRepository;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    public PersonServiceImpl(PersonRepository personRepository, PersonMapper personMapper) {
        this.personRepository = personRepository;
        this.personMapper = personMapper;
    }

    @Override
    public ResponseEntity<List<PersonResponse>> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        return ResponseEntity.ok(personMapper.toResponseList(persons));
    }

    @Override
    public ResponseEntity<PersonResponse> getPersonById(IdRequest request) {
        Optional<Person> personOpt = personRepository.findById(request.getId());
        return personOpt
                .map(person -> ResponseEntity.ok(personMapper.toResponse(person)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<PersonResponse> createPerson(CreatePersonRequest dto) {
        Person entity = personMapper.toEntity(dto);
        Person saved = personRepository.save(entity);
        return ResponseEntity.ok(personMapper.toResponse(saved));
    }

    @Override
    public ResponseEntity<PersonResponse> updatePerson(UpdatePersonRequest dto) {
        Optional<Person> existingOpt = personRepository.findById(dto.getId());
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Person existing = existingOpt.get();
        personMapper.updateEntity(existing, dto);
        Person updated = personRepository.save(existing);
        return ResponseEntity.ok(personMapper.toResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deletePerson(IdRequest request) {
        if (!personRepository.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        personRepository.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<PersonResponse>> getPersonsByGender(String gender) {
        List<Person> persons = personRepository.findByGenderIgnoreCase(gender);
        return ResponseEntity.ok(personMapper.toResponseList(persons));
    }

    @Override
    public ResponseEntity<List<PersonResponse>> getPersonsBornBefore(String date) {
        LocalDate birthDate = LocalDate.parse(date);
        List<Person> persons = personRepository.findByBirthDateBefore(birthDate);
        return ResponseEntity.ok(personMapper.toResponseList(persons));
    }

    @Override
    public ResponseEntity<List<PersonResponse>> getPersonsByMaritalStatus(String status) {
        List<Person> persons = personRepository.findByMaritalStatusIgnoreCase(status);
        return ResponseEntity.ok(personMapper.toResponseList(persons));
    }

    @Override
    public ResponseEntity<PersonResponse> getPersonByNationalId(String nationalId) {
        Optional<Person> personOpt = personRepository.findByNationalId(nationalId);
        return personOpt
                .map(person -> ResponseEntity.ok(personMapper.toResponse(person)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<PersonResponse>> searchPersonsByName(String firstName, String lastName) {
        List<Person> persons = personRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                firstName != null ? firstName : "",
                lastName != null ? lastName : ""
        );
        return ResponseEntity.ok(personMapper.toResponseList(persons));
    }

    @Override
    public ResponseEntity<PersonResponse> getPersonByEmail(String email) {
        Optional<Person> personOpt = personRepository.findByEmailIgnoreCase(email);
        return personOpt
                .map(person -> ResponseEntity.ok(personMapper.toResponse(person)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public boolean existsByNationalId(String nationalId) {
        return false;
    }

    @Override
    public Optional<Person> findByEmailIgnoreCase(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<Person> findByNationalId(String nationalId) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public List<Person> searchByOptionalNames(String firstName, String lastName) {
        if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
            return personRepository.findAll();
        } else if (firstName != null && !firstName.isBlank() && (lastName == null || lastName.isBlank())) {
            return personRepository.findByFirstNameContainingIgnoreCase(firstName);
        } else if ((firstName == null || firstName.isBlank()) && lastName != null && !lastName.isBlank()) {
            return personRepository.findByLastNameContainingIgnoreCase(lastName);
        } else {
            return personRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
        }
    }

}
