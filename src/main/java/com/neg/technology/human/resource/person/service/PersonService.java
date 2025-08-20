package com.neg.technology.human.resource.person.service;

import com.neg.technology.human.resource.person.model.entity.Person;
import com.neg.technology.human.resource.person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.person.model.response.PersonResponse;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PersonService {

    Mono<List<PersonResponse>> getAllPersons();

    Mono<PersonResponse> getPersonById(IdRequest request);

    Mono<PersonResponse> createPerson(CreatePersonRequest dto);

    Mono<PersonResponse> updatePerson(UpdatePersonRequest dto);

    Mono<Void> deletePerson(IdRequest request);

    Mono<List<PersonResponse>> getPersonsByGender(String gender);

    Mono<List<PersonResponse>> getPersonsBornBefore(String date);

    Mono<List<PersonResponse>> getPersonsByMaritalStatus(String status);

    Mono<PersonResponse> getPersonByNationalId(String nationalId);

    Mono<List<PersonResponse>> searchPersonsByName(String firstName, String lastName);

    Mono<PersonResponse> getPersonByEmail(String email);

    Mono<Boolean> existsByEmail(String email);

    Mono<Boolean> existsByNationalId(String nationalId);

    Mono<Person> findByEmailIgnoreCase(String email);

    Mono<Person> findByNationalId(String nationalId);

    Mono<Boolean> existsById(Long id);

    Mono<List<Person>> searchByOptionalNames(String firstName, String lastName);
}