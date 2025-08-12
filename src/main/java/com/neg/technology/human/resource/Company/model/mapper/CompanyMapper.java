package com.neg.technology.human.resource.Company.model.mapper;

import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.response.CompanyResponse;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.model.entity.Company;

public class CompanyMapper {

    public static CompanyResponse toDTO(Company company) {
        if (company == null) return null;
        return new CompanyResponse(company.getId(), company.getName());
    }

    public static Company toEntity(CreateCompanyRequest dto) {
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
