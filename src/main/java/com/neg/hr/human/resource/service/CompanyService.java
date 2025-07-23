package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Company;
import com.neg.hr.human.resource.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService implements CompanyInterface {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public Optional<Company> findById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public Optional<Company> findByName(String name) {
        return companyRepository.findByName(name);
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    @Override
    public boolean existsByName(String name) {
        return companyRepository.existsByName(name);
    }

    @Override
    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public Company update(Long id, Company company) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id " + id));

        existing.setName(company.getName());

        return companyRepository.save(existing);
    }
}
