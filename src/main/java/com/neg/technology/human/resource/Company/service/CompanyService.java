package com.neg.technology.human.resource.Company.service;

import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.model.response.CompanyResponse;
import com.neg.technology.human.resource.Company.model.response.CompanyResponseList;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;

public interface CompanyService {

    CompanyResponse createCompany(CreateCompanyRequest request);

    CompanyResponse updateCompany(UpdateCompanyRequest request);

    void deleteCompany(IdRequest request);

    CompanyResponseList getAllCompanies();

    CompanyResponse getCompanyById(IdRequest request);

    CompanyResponse getCompanyByName(NameRequest request);

    boolean existsByName(NameRequest request);
}
