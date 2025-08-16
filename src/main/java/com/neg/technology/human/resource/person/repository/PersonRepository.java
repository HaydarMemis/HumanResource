package com.neg.technology.human.resource.person.repository;

import com.neg.technology.human.resource.person.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByNationalId(String nationalId);

    List<Person> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);

    Optional<Person> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    List<Person> findByGenderIgnoreCase(String gender);

    List<Person> findByBirthDateBefore(LocalDate birthDate);

    List<Person> findByMaritalStatusIgnoreCase(String maritalStatus);

    List<Person> findByFirstNameContainingIgnoreCase(String firstName);
    List<Person> findByLastNameContainingIgnoreCase(String lastName);
}
