package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyInterface {
    Company save(Company company);

    Optional<Company> findById(Long id);

    Optional<Company> findByName(String name);

    List<Company> findAll();

    boolean existsByName(String name);

    void deleteById(Long id);

    Company update(Long id, Company company);
}
