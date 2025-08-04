package com.neg.hr.human.resource.business;

import com.neg.hr.human.resource.dto.create.CreateCompanyDTO;
import com.neg.hr.human.resource.dto.update.UpdateCompanyDTO;
import com.neg.hr.human.resource.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CompanyValidator {

    private final CompanyService companyService;

    public CompanyValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    public void validateCreate(CreateCompanyDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Company name must not be empty");
        }
        if (companyService.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Company name already exists");
        }
    }

    public void validateUpdate(UpdateCompanyDTO dto, Long id) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Company name must not be empty");
        }
        companyService.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Company name already exists");
            }
        });
    }
}
