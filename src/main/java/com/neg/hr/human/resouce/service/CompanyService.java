package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Company;
import com.neg.hr.human.resouce.entity.LeaveBalance;
import com.neg.hr.human.resouce.repository.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService implements CompanyInterface {
    private final CompanyRepository companyRepository;

    public Company save(Company company) {
        return companyRepository.save(company);
    }

    public Company findById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public void delete(Long id) {
        companyRepository.deleteById(id);
    }

    public Company update(Long id, Company company) {
        Company existing = findById(id);
        existing.setName(company.getName());
        return companyRepository.save(existing);
    }

}
