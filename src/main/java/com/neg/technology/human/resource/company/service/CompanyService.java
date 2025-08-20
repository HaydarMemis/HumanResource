package com.neg.technology.human.resource.company.service;

import com.neg.technology.human.resource.company.model.request.*;
import com.neg.technology.human.resource.company.model.response.*;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import reactor.core.publisher.Mono;

public interface CompanyService {

    Mono<CompanyResponse> createCompany(CreateCompanyRequest request);

    Mono<CompanyResponse> updateCompany(UpdateCompanyRequest request);

    Mono<Void> deleteCompany(CompanyIdRequest request);

    Mono<CompanyResponseList> getAllCompanies();

    Mono<CompanyResponse> getCompanyById(CompanyIdRequest request);

    Mono<CompanyResponse> getCompanyByName(NameRequest request);

    Mono<Boolean> existsByName(NameRequest request);
}

