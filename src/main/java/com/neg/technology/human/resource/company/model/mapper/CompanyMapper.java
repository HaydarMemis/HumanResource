package com.neg.technology.human.resource.company.model.mapper;

import com.neg.technology.human.resource.company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.company.model.response.CompanyResponse;
import com.neg.technology.human.resource.company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.company.model.entity.Company;
import jakarta.validation.Valid;

public class CompanyMapper {

    private CompanyMapper() {}

    public static CompanyResponse toDTO(Company company) {
        if (company == null) return null;
        return new CompanyResponse(company.getId(), company.getName());
    }

    public static Company toEntity(@Valid UpdateCompanyRequest dto) {
        if (dto == null) return null;
        return Company.builder()
                .name(dto.getName())
                .build();
    }

    public static Company toEntity(@Valid CreateCompanyRequest dto) {
        if (dto == null) return null;
        return Company.builder()
                .name(dto.getName())
                .build();
    }

    public static void updateEntity(Company company, UpdateCompanyRequest dto) {
        if (company == null || dto == null) return;
        if (dto.getName() != null) company.setName(dto.getName());
    }
}
