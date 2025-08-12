package com.neg.technology.human.resource.Company.service;

import com.neg.technology.human.resource.Company.model.entity.Company;
import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.model.response.CompanyResponse;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    CompanyResponse createCompany(CreateCompanyRequest request);

    Optional<CompanyResponse> updateCompany(UpdateCompanyRequest request);

    Optional<Company> findByName(String name);

    void deleteCompany(Long id);

    List<CompanyResponse> getAllCompanies();

    Optional<CompanyResponse> getCompanyById(IdRequest request);

    Optional<CompanyResponse> getCompanyByName(NameRequest request);

    boolean existsByName(String name);
}
