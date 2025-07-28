package com.neg.hr.human.resource.service.impl;

import com.neg.hr.human.resource.entity.Company;
import com.neg.hr.human.resource.exception.ResourceNotFoundException;
import com.neg.hr.human.resource.repository.CompanyRepository;
import com.neg.hr.human.resource.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
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
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company",id);
        }
        companyRepository.deleteById(id);
    }

    @Override
    public Company update(Long id, Company company) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));

        existing.setName(company.getName());

        return companyRepository.save(existing);
    }
}
