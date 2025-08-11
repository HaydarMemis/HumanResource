package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonService {
    Optional<Person> findByNationalId(String nationalId);

    List<Person> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);

    Optional<Person> findByEmailIgnoreCase(String email);

    List<Person> findByGenderIgnoreCase(String gender);

    List<Person> findByBirthDateBefore(LocalDate birthDate);

    List<Person> findByMaritalStatusIgnoreCase(String maritalStatus);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    Person save(Person person);

    Optional<Person> findById(Long id);

    List<Person> findAll();

    void deleteById(Long id);

    Person update(Long id, Person leaveBalance);

    boolean existsById(Long id);

    List<Person> searchByOptionalNames(String firstName, String lastName);
}
