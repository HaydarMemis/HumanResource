package com.neg.technology.human.resource.company.validator;

import com.neg.technology.human.resource.company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.company.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CompanyValidator {

    private final CompanyRepository companyRepository;

    public CompanyValidator(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public void validateCreate(CreateCompanyRequest dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Company name must not be empty");
        }
        if (companyRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Company name already exists");
        }
    }

    public void validateUpdate(UpdateCompanyRequest dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Company name must not be empty");
        }
        companyRepository.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(dto.getId())) {
                throw new IllegalArgumentException("Company name already exists");
            }
        });
    }
}
