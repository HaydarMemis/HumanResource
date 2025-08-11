package com.neg.technology.human.resource.Company.service;

import com.neg.technology.human.resource.business.BusinessLogger;
import com.neg.technology.human.resource.Company.model.entity.Company;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Company.repository.CompanyRepository;
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
        Company saved = companyRepository.save(company);
        BusinessLogger.logCreated(Company.class, saved.getId(), saved.getName());
        return saved;
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
        BusinessLogger.logDeleted(Company.class, id);
    }

    @Override
    public Company update(Long id, Company company) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));

        existing.setName(company.getName());

       Company updated = companyRepository.save(existing);
       BusinessLogger.logUpdated(Company.class, updated.getId(), updated.getName());
       return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return companyRepository.existsById(id);
    }
}
