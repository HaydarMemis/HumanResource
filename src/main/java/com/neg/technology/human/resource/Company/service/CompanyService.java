package com.neg.technology.human.resource.Company.service;

import com.neg.technology.human.resource.Company.model.entity.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    Company save(Company company);

    Optional<Company> findById(Long id);

    Optional<Company> findByName(String name);

    List<Company> findAll();

    boolean existsByName(String name);

    void deleteById(Long id);

    Company update(Long id, Company company);

    boolean existsById(Long id);
}
