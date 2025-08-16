package com.neg.technology.human.resource.company.service.impl;

import com.neg.technology.human.resource.company.model.entity.Company;
import com.neg.technology.human.resource.company.model.mapper.CompanyMapper;
import com.neg.technology.human.resource.company.model.request.CompanyIdRequest;
import com.neg.technology.human.resource.company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.company.model.response.CompanyResponse;
import com.neg.technology.human.resource.company.model.response.CompanyResponseList;
import com.neg.technology.human.resource.company.repository.CompanyRepository;
import com.neg.technology.human.resource.company.service.CompanyService;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl implements CompanyService {
    public static final String MESSAGE ="Company";

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        Company entity = CompanyMapper.toEntity(request);
        Company saved = companyRepository.save(entity);
        Logger.logCreated(Company.class, saved.getId(), saved.getName());
        return CompanyMapper.toDTO(saved);
    }

    @Override
    public CompanyResponse updateCompany(UpdateCompanyRequest request) {
        Company existing = companyRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

        existing.setName(request.getName());
        Company updated = companyRepository.save(existing);
        Logger.logUpdated(Company.class, updated.getId(), updated.getName());

        return CompanyMapper.toDTO(updated);
    }

    @Override
    public void deleteCompany(CompanyIdRequest request) {
        if (!companyRepository.existsById(request.getCompanyId())) {
            throw new ResourceNotFoundException(MESSAGE, request.getCompanyId());
        }
        companyRepository.deleteById(request.getCompanyId());
        Logger.logDeleted(Company.class, request.getCompanyId());
    }

    @Override
    public CompanyResponseList getAllCompanies() {
        return new CompanyResponseList(
                companyRepository.findAll()
                        .stream()
                        .map(CompanyMapper::toDTO)
                        .toList()
        );
    }

    @Override
    public CompanyResponse getCompanyById(CompanyIdRequest request) {
        return companyRepository.findById(request.getCompanyId())
                .map(CompanyMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getCompanyId()));
    }

    @Override
    public CompanyResponse getCompanyByName(NameRequest request) {
        return companyRepository.findByName(request.getName())
                .map(CompanyMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getName()));
    }

    @Override
    public boolean existsByName(NameRequest request) {
        return companyRepository.existsByName(request.getName());
    }
}
