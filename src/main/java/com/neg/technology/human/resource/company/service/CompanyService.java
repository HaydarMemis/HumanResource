package com.neg.technology.human.resource.company.service;

import com.neg.technology.human.resource.company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.company.model.response.CompanyResponse;
import com.neg.technology.human.resource.company.model.response.CompanyResponseList;
import com.neg.technology.human.resource.company.model.request.CompanyIdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;

public interface CompanyService {

    CompanyResponse createCompany(CreateCompanyRequest request);

    CompanyResponse updateCompany(UpdateCompanyRequest request);

    void deleteCompany(CompanyIdRequest request);

    CompanyResponseList getAllCompanies();

    CompanyResponse getCompanyById(CompanyIdRequest request);

    CompanyResponse getCompanyByName(NameRequest request);

    boolean existsByName(NameRequest request);
}
