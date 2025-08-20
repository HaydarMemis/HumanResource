package com.neg.technology.human.resource.person.service;

import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.person.model.entity.Person;
import com.neg.technology.human.resource.person.model.mapper.PersonMapper;
import com.neg.technology.human.resource.person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.person.model.response.PersonResponse;
import com.neg.technology.human.resource.person.repository.PersonRepository;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

    @Override
    public Mono<List<PersonResponse>> getAllPersons() {
        return Mono.fromCallable(() -> {
            List<Person> persons = personRepository.findAll();
            return personMapper.toResponseList(persons);
        });
    }

    @Override
    public Mono<PersonResponse> getPersonById(IdRequest request) {
        return Mono.fromCallable(() ->
                personRepository.findById(request.getId())
                        .map(personMapper::toResponse)
                        .orElseThrow(() -> new RuntimeException("Person not found with ID: " + request.getId()))
        );
    }

    @Override
    public Mono<PersonResponse> createPerson(CreatePersonRequest dto) {
        return Mono.fromCallable(() -> {
            Person entity = personMapper.toEntity(dto);
            Person saved = personRepository.save(entity);
            return personMapper.toResponse(saved);
        });
    }

    @Override
    public Mono<PersonResponse> updatePerson(UpdatePersonRequest dto) {
        return Mono.fromCallable(() -> {
            Person existing = personRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Person not found with ID: " + dto.getId()));

            personMapper.updateEntity(existing, dto);
            Person updated = personRepository.save(existing);
            return personMapper.toResponse(updated);
        });
    }

    @Override
    public Mono<Void> deletePerson(IdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!personRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException(request.getClass().getName(),request.getId());
            }
            personRepository.deleteById(request.getId());
        });
    }

    @Override
    public Mono<List<PersonResponse>> getPersonsByGender(String gender) {
        return Mono.fromCallable(() -> {
            List<Person> persons = personRepository.findByGenderIgnoreCase(gender);
            return personMapper.toResponseList(persons);
        });
    }

    @Override
    public Mono<List<PersonResponse>> getPersonsBornBefore(String date) {
        return Mono.fromCallable(() -> {
            LocalDate birthDate = LocalDate.parse(date);
            List<Person> persons = personRepository.findByBirthDateBefore(birthDate);
            return personMapper.toResponseList(persons);
        });
    }

    @Override
    public Mono<List<PersonResponse>> getPersonsByMaritalStatus(String status) {
        return Mono.fromCallable(() -> {
            List<Person> persons = personRepository.findByMaritalStatusIgnoreCase(status);
            return personMapper.toResponseList(persons);
        });
    }

    @Override
    public Mono<PersonResponse> getPersonByNationalId(String nationalId) {
        return Mono.fromCallable(() ->
                personRepository.findByNationalId(nationalId)
                        .map(personMapper::toResponse)
                        .orElseThrow(() -> new RuntimeException("Person not found with national ID: " + nationalId))
        );
    }

    @Override
    public Mono<List<PersonResponse>> searchPersonsByName(String firstName, String lastName) {
        return Mono.fromCallable(() -> {
            List<Person> persons = personRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                    firstName != null ? firstName : "",
                    lastName != null ? lastName : ""
            );
            return personMapper.toResponseList(persons);
        });
    }

    @Override
    public Mono<PersonResponse> getPersonByEmail(String email) {
        return Mono.fromCallable(() ->
                personRepository.findByEmailIgnoreCase(email)
                        .map(personMapper::toResponse)
                        .orElseThrow(() -> new RuntimeException("Person not found with email: " + email))
        );
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return Mono.fromCallable(() ->
                personRepository.existsByEmail(email)
        );
    }

    @Override
    public Mono<Boolean> existsByNationalId(String nationalId) {
        return Mono.fromCallable(() ->
                personRepository.existsByNationalId(nationalId)
        );
    }

    @Override
    public Mono<Person> findByEmailIgnoreCase(String email) {
        return Mono.fromCallable(() ->
                personRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new RuntimeException("Person not found with email: " + email))
        );
    }

    @Override
    public Mono<Person> findByNationalId(String nationalId) {
        return Mono.fromCallable(() ->
                personRepository.findByNationalId(nationalId)
                        .orElseThrow(() -> new RuntimeException("Person not found with national ID: " + nationalId))
        );
    }

    @Override
    public Mono<Boolean> existsById(Long id) {
        return Mono.fromCallable(() ->
                personRepository.existsById(id)
        );
    }

    @Override
    public Mono<List<Person>> searchByOptionalNames(String firstName, String lastName) {
        return Mono.fromCallable(() -> {
            if ((firstName == null || firstName.isBlank()) && (lastName == null || lastName.isBlank())) {
                return personRepository.findAll();
            } else if (firstName != null && !firstName.isBlank() && (lastName == null || lastName.isBlank())) {
                return personRepository.findByFirstNameContainingIgnoreCase(firstName);
            } else if ((firstName == null || firstName.isBlank()) && lastName != null && !lastName.isBlank()) {
                return personRepository.findByLastNameContainingIgnoreCase(lastName);
            } else {
                return personRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(firstName, lastName);
            }
        });
    }
}