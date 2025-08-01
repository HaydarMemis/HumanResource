package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.business.CompanyValidator;
import com.neg.hr.human.resource.dto.CompanyDTO;
import com.neg.hr.human.resource.dto.CreateCompanyDTO;
import com.neg.hr.human.resource.dto.UpdateCompanyDTO;
import com.neg.hr.human.resource.entity.Company;
import com.neg.hr.human.resource.mapper.CompanyMapper;
import com.neg.hr.human.resource.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyValidator companyValidator;

    public CompanyController(CompanyService companyService, CompanyValidator companyValidator) {
        this.companyService = companyService;
        this.companyValidator = companyValidator;
    }

    @GetMapping
    public List<CompanyDTO> getAllCompanies() {
        return companyService.findAll()
                .stream()
                .map(CompanyMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Long id) {
        Optional<Company> opt = companyService.findById(id);
        return opt.map(company -> ResponseEntity.ok(CompanyMapper.toDTO(company)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CompanyDTO> getCompanyByName(@PathVariable String name) {
        Optional<Company> opt = companyService.findByName(name);
        return opt.map(company -> ResponseEntity.ok(CompanyMapper.toDTO(company)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CreateCompanyDTO dto) {
        companyValidator.validateCreate(dto);
        Company company = CompanyMapper.toEntity(dto);
        Company saved = companyService.save(company);
        return ResponseEntity.ok(CompanyMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Long id, @Valid @RequestBody UpdateCompanyDTO dto) {
        Optional<Company> existingOpt = companyService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        companyValidator.validateUpdate(dto, id);
        Company existing = existingOpt.get();
        CompanyMapper.updateEntity(existing, dto);
        Company updated = companyService.save(existing);
        return ResponseEntity.ok(CompanyMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        Optional<Company> existingOpt = companyService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        companyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{name}")
    public boolean existsByName(@PathVariable String name) {
        return companyService.existsByName(name);
    }
}
