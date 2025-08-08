package com.neg.hr.human.resource.validator;

import com.neg.hr.human.resource.dto.create.CreateCompanyRequestDTO;
import com.neg.hr.human.resource.dto.update.UpdateCompanyRequestDTO;
import com.neg.hr.human.resource.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CompanyValidator {

    private final CompanyService companyService;

    public CompanyValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    public void validateCreate(CreateCompanyRequestDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new IllegalArgumentException("Company name must not be empty");
        }
        if (companyService.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Company name already exists");
        }
    }

    public void validateUpdate(UpdateCompanyRequestDTO dto, Long id) {
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
