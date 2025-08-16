package com.neg.technology.human.resource.person.service;

import com.neg.technology.human.resource.person.model.entity.Person;
import com.neg.technology.human.resource.person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.person.model.response.PersonResponse;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PersonService {

    ResponseEntity<List<PersonResponse>> getAllPersons();

    ResponseEntity<PersonResponse> getPersonById(IdRequest request);

    ResponseEntity<PersonResponse> createPerson(CreatePersonRequest dto);

    ResponseEntity<PersonResponse> updatePerson(UpdatePersonRequest dto);

    ResponseEntity<Void> deletePerson(IdRequest request);

    ResponseEntity<List<PersonResponse>> getPersonsByGender(String gender);

    ResponseEntity<List<PersonResponse>> getPersonsBornBefore(String date);

    ResponseEntity<List<PersonResponse>> getPersonsByMaritalStatus(String status);

    ResponseEntity<PersonResponse> getPersonByNationalId(String nationalId);

    ResponseEntity<List<PersonResponse>> searchPersonsByName(String firstName, String lastName);

    ResponseEntity<PersonResponse> getPersonByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    Optional<com.neg.technology.human.resource.person.model.entity.Person> findByEmailIgnoreCase(String email);

    Optional<com.neg.technology.human.resource.person.model.entity.Person> findByNationalId(String nationalId);

    boolean existsById(Long id);

    List<Person> searchByOptionalNames(String firstName, String lastName);
}
