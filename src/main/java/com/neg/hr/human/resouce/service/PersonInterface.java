package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.LeaveBalance;
import com.neg.hr.human.resouce.entity.Person;

import java.util.List;
import java.util.Optional;

public interface PersonInterface {
    Optional<Person> findByNationalId(String nationalId);

    List<Person> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(String firstName, String lastName);

    Optional<Person> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByNationalId(String nationalId);

    public LeaveBalance save(LeaveBalance leaveBalance);

    public Optional<LeaveBalance> findById(Long id);

    public List<LeaveBalance> findAll();

    void deleteById(Long id);

    public LeaveBalance update(Long id, LeaveBalance leaveBalance);
}
