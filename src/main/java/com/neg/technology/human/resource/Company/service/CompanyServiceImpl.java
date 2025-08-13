package com.neg.technology.human.resource.Company.service;

import com.neg.technology.human.resource.Utility.RequestLogger;
import com.neg.technology.human.resource.Company.model.entity.Company;
import com.neg.technology.human.resource.Company.model.mapper.CompanyMapper;
import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.model.response.CompanyResponse;
import com.neg.technology.human.resource.Company.model.response.CompanyResponseList;
import com.neg.technology.human.resource.Company.repository.CompanyRepository;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Utility.request.CompanyIdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public CompanyResponse createCompany(CreateCompanyRequest request) {
        Company entity = CompanyMapper.toEntity(request);
        Company saved = companyRepository.save(entity);
        RequestLogger.logCreated(Company.class, saved.getId(), saved.getName());
        return CompanyMapper.toDTO(saved);
    }

    @Override
    public CompanyResponse updateCompany(UpdateCompanyRequest request) {
        Company existing = companyRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", request.getId()));

        existing.setName(request.getName());
        Company updated = companyRepository.save(existing);
        RequestLogger.logUpdated(Company.class, updated.getId(), updated.getName());

        return CompanyMapper.toDTO(updated);
    }

    @Override
    public void deleteCompany(CompanyIdRequest request) {
        if (!companyRepository.existsById(request.getCompanyId())) {
            throw new ResourceNotFoundException("Company", request.getCompanyId());
        }
        companyRepository.deleteById(request.getCompanyId());
        RequestLogger.logDeleted(Company.class, request.getCompanyId());
    }

    @Override
    public CompanyResponseList getAllCompanies() {
        return new CompanyResponseList(
                companyRepository.findAll()
                        .stream()
                        .map(CompanyMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public CompanyResponse getCompanyById(CompanyIdRequest request) {
        return companyRepository.findById(request.getCompanyId())
                .map(CompanyMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Company", request.getCompanyId()));
    }

    @Override
    public CompanyResponse getCompanyByName(NameRequest request) {
        return companyRepository.findByName(request.getName())
                .map(CompanyMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Company", request.getName()));
    }

    @Override
    public boolean existsByName(NameRequest request) {
        return companyRepository.existsByName(request.getName());
    }
}
