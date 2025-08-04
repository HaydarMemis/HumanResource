package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.*;
import com.neg.hr.human.resource.dto.create.CreateCompanyDTO;
import com.neg.hr.human.resource.dto.update.UpdateCompanyDTO;
import com.neg.hr.human.resource.entity.Company;

public class CompanyMapper {

    public static CompanyDTO toDTO(Company company) {
        if (company == null) return null;
        return new CompanyDTO(company.getId(), company.getName());
    }

    public static Company toEntity(CreateCompanyDTO dto) {
        if (dto == null) return null;
        return Company.builder()
                .name(dto.getName())
                .build();
    }

    public static void updateEntity(Company company, UpdateCompanyDTO dto) {
        if (company == null || dto == null) return;
        if (dto.getName() != null) company.setName(dto.getName());
    }
}
