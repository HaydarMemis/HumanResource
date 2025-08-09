package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.entity.CompanyEntityDTO;
import com.neg.hr.human.resource.dto.IdRequest;
import com.neg.hr.human.resource.dto.NameRequestDTO;
import com.neg.hr.human.resource.dto.create.CreateCompanyRequestDTO;
import com.neg.hr.human.resource.dto.update.UpdateCompanyRequestDTO;
import com.neg.hr.human.resource.entity.Company;
import com.neg.hr.human.resource.mapper.CompanyMapper;
import com.neg.hr.human.resource.service.CompanyService;
import com.neg.hr.human.resource.validator.CompanyValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyValidator companyValidator;

    public CompanyController(CompanyService companyService,
                             CompanyValidator companyValidator) {
        this.companyService = companyService;
        this.companyValidator = companyValidator;
    }

    @PostMapping("/getAll")
    public ResponseEntity<List<CompanyEntityDTO>> getAllCompanies() {
        List<CompanyEntityDTO> companies = companyService.findAll().stream()
                .map(CompanyMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(companies);
    }

    @PostMapping("/getById")
    public ResponseEntity<CompanyEntityDTO> getCompanyById(
            @Valid @RequestBody IdRequest request) {
        return companyService.findById(request.getId())
                .map(company -> ResponseEntity.ok(CompanyMapper.toDTO(company)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/getByName")
    public ResponseEntity<CompanyEntityDTO> getCompanyByName(
            @Valid @RequestBody NameRequestDTO request) {
        return companyService.findByName(request.getName())
                .map(company -> ResponseEntity.ok(CompanyMapper.toDTO(company)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<CompanyEntityDTO> createCompany(
            @Valid @RequestBody CreateCompanyRequestDTO dto) {
        companyValidator.validateCreate(dto);
        Company company = CompanyMapper.toEntity(dto);
        Company saved = companyService.save(company);
        return ResponseEntity.ok(CompanyMapper.toDTO(saved));
    }

    @PostMapping("/update")
    public ResponseEntity<CompanyEntityDTO> updateCompany(
            @PathVariable Long Id, @Valid @RequestBody UpdateCompanyRequestDTO dto) {
        companyValidator.validateUpdate(dto, Id);

        // 1. Var plan company bul
        Company existingCompany = companyService.findById(Id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        CompanyMapper.updateEntity(existingCompany, dto);

        // 3. excellence entity ile update yap
        Company updated = companyService.update(Id, existingCompany);

        return ResponseEntity.ok(CompanyMapper.toDTO(updated));
    }


    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCompany(
            @Valid @RequestBody IdRequest request) {
        companyService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exists")
    public ResponseEntity<Boolean> companyExists(
            @Valid @RequestBody NameRequestDTO request) {
        boolean exists = companyService.existsByName(request.getName());
        return ResponseEntity.ok(exists);
    }
}