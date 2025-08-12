package com.neg.technology.human.resource.Company.controller;

import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.response.CompanyResponse;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.model.entity.Company;
import com.neg.technology.human.resource.Company.model.mapper.CompanyMapper;
import com.neg.technology.human.resource.Company.service.CompanyService;
import com.neg.technology.human.resource.Company.validator.CompanyValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Company Controller", description = "Operations related to company management")
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyValidator companyValidator;

    public CompanyController(CompanyService companyService, CompanyValidator companyValidator) {
        this.companyService = companyService;
        this.companyValidator = companyValidator;
    }

    @PostMapping("/create")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        companyValidator.validateCreate(request);
        Company company = CompanyMapper.toEntity(request);
        Company saved = companyService.save(company);
        return ResponseEntity.ok(CompanyMapper.toDTO(saved));
    }

    @PostMapping("/update")
    public ResponseEntity<CompanyResponse> updateCompany(@Valid @RequestBody UpdateCompanyRequest request) {
        companyValidator.validateUpdate(request);
        Company updated = companyService.update(request.getId(), CompanyMapper.toEntity(request));
        return ResponseEntity.ok(CompanyMapper.toDTO(updated));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCompany(@Valid @RequestBody IdRequest request) {
        companyService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/getAll")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        List<CompanyResponse> list = companyService.findAll().stream()
                .map(CompanyMapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/getById")
    public ResponseEntity<CompanyResponse> getCompanyById(@Valid @RequestBody IdRequest request) {
        return companyService.findById(request.getId())
                .map(company -> ResponseEntity.ok(CompanyMapper.toDTO(company)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/getByName")
    public ResponseEntity<CompanyResponse> getCompanyByName(@Valid @RequestBody NameRequest request) {
        return companyService.findByName(request.getName())
                .map(company -> ResponseEntity.ok(CompanyMapper.toDTO(company)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/exists")
    public ResponseEntity<Boolean> companyExists(@Valid @RequestBody NameRequest request) {
        boolean exists = companyService.existsByName(request.getName());
        return ResponseEntity.ok(exists);
    }
}

