package com.neg.technology.human.resource.Company.service;

import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.Company.model.entity.Company;
import com.neg.technology.human.resource.Company.model.mapper.CompanyMapper;
import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.model.response.CompanyResponse;
import com.neg.technology.human.resource.Company.repository.CompanyRepository;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        BusinessLogger.logCreated(Company.class, saved.getId(), saved.getName());
        return CompanyMapper.toDTO(saved);
    }

    @Override
    public Optional<CompanyResponse> updateCompany(UpdateCompanyRequest request) {
        return companyRepository.findById(request.getId())
                .map(existing -> {
                    existing.setName(request.getName());
                    Company updated = companyRepository.save(existing);
                    BusinessLogger.logUpdated(Company.class, updated.getId(), updated.getName());
                    return CompanyMapper.toDTO(updated);
                });
    }

    @Override
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company", id);
        }
        companyRepository.deleteById(id);
        BusinessLogger.logDeleted(Company.class, id);
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(CompanyMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CompanyResponse> getCompanyById(IdRequest request) {
        return companyRepository.findById(request.getId())
                .map(CompanyMapper::toDTO);
    }

    @Override
    public Optional<CompanyResponse> getCompanyByName(NameRequest request) {
        return companyRepository.findByName(request.getName())
                .map(CompanyMapper::toDTO);
    }

    @Override
    public boolean existsByName(String name) {
        return companyRepository.existsByName(name);
    }
}
