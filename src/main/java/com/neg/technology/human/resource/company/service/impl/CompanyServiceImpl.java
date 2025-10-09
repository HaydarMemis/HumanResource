package com.neg.technology.human.resource.company.service.impl;

import com.neg.technology.human.resource.company.model.entity.Company;
import com.neg.technology.human.resource.company.model.mapper.CompanyMapper;
import com.neg.technology.human.resource.company.model.request.*;
import com.neg.technology.human.resource.company.model.response.*;
import com.neg.technology.human.resource.company.repository.CompanyRepository;
import com.neg.technology.human.resource.company.service.CompanyService;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Mono<CompanyResponse> createCompany(CreateCompanyRequest request) {
        return Mono.fromCallable(() -> {
            Company entity = CompanyMapper.toEntity(request);
            Company saved = companyRepository.save(entity);
            Logger.logCreated(Company.class, saved.getId(), saved.getName());
            return CompanyMapper.toDTO(saved);
        });
    }

    @Override
    public Mono<CompanyResponse> updateCompany(UpdateCompanyRequest request) {
        return Mono.fromCallable(() -> {
            Company existing = companyRepository.findById(request.getId())
                    .orElseThrow(ResourceNotFoundException::companyNotFound);

            existing.setName(request.getName());
            Company updated = companyRepository.save(existing);
            Logger.logUpdated(Company.class, updated.getId(), updated.getName());

            return CompanyMapper.toDTO(updated);
        });
    }

    @Override
    public Mono<Void> deleteCompany(CompanyIdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!companyRepository.existsById(request.getCompanyId())) {
                throw ResourceNotFoundException.companyNotFound();
            }
            companyRepository.deleteById(request.getCompanyId());
            Logger.logDeleted(Company.class, request.getCompanyId());
        });
    }

    @Override
    public Mono<CompanyResponseList> getAllCompanies() {
        return Mono.fromCallable(() ->
                new CompanyResponseList(
                        companyRepository.findAll()
                                .stream()
                                .map(CompanyMapper::toDTO)
                                .toList()
                )
        );
    }

    @Override
    public Mono<CompanyResponse> getCompanyById(CompanyIdRequest request) {
        return Mono.fromCallable(() ->
                companyRepository.findById(request.getCompanyId())
                        .map(CompanyMapper::toDTO)
                        .orElseThrow(ResourceNotFoundException::companyNotFound)
        );
    }

    @Override
    public Mono<CompanyResponse> getCompanyByName(NameRequest request) {
        return Mono.fromCallable(() ->
                companyRepository.findByName(request.getName())
                        .map(CompanyMapper::toDTO)
                        .orElseThrow(ResourceNotFoundException::companyNameNotFound)
        );
    }

    @Override
    public Mono<Boolean> existsByName(NameRequest request) {
        return Mono.fromCallable(() -> companyRepository.existsByName(request.getName()));
    }
}
