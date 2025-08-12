package com.neg.technology.human.resource.Company.validator;

import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CompanyValidator {

    private final CompanyService companyService;

    public CompanyValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    public void validateCreate(CreateCompanyRequest dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Company name must not be empty");
        }
        if (companyService.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Company name already exists");
        }
    }

    public void validateUpdate(UpdateCompanyRequest dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Company name must not be empty");
        }
        companyService.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(dto.getId())) {
                throw new IllegalArgumentException("Company name already exists");
            }
        });
    }
}