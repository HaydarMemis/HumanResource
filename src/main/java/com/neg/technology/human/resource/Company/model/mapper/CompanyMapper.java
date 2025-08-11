package com.neg.technology.human.resource.Company.model.mapper;

import com.neg.technology.human.resource.dto.create.CreateCompanyRequestDTO;
import com.neg.technology.human.resource.dto.entity.CompanyEntityDTO;
import com.neg.technology.human.resource.dto.update.UpdateCompanyRequestDTO;
import com.neg.technology.human.resource.Company.model.entity.Company;

public class CompanyMapper {

    public static CompanyEntityDTO toDTO(Company company) {
        if (company == null) return null;
        return new CompanyEntityDTO(company.getId(), company.getName());
    }

    public static Company toEntity(CreateCompanyRequestDTO dto) {
        if (dto == null) return null;
        return Company.builder()
                .name(dto.getName())
                .build();
    }

    public static void updateEntity(Company company, UpdateCompanyRequestDTO dto) {
        if (company == null || dto == null) return;
        if (dto.getName() != null) company.setName(dto.getName());
    }
}
