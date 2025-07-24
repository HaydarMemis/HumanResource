package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Person;
import com.neg.hr.human.resource.entity.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PersonInterface {
    Optional<Person> findByNationalId(String nationalId);

    List<Person> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);

    Optional<Person> findByEmailIgnoreCase(String email);

    List<Person> findByGenderIgnoreCase(String gender);

    List<Person> findByBirthDateBefore(LocalDate birthDate);

    List<Person> findByMaritalStatusIgnoreCase(String maritalStatus);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    public Person save(Person person);

    public Optional<Person> findById(Long id);

    public List<Person> findAll();

    void deleteById(Long id);

    public Person update(Long id, Person leaveBalance);
}
