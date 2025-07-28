package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.entity.Company;
import com.neg.hr.human.resource.service.impl.CompanyServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyServiceImpl companyService;

    public CompanyController(CompanyServiceImpl companyService) {
        this.companyService = companyService;
    }

    // Tüm şirketleri getir
    @GetMapping
    public List<Company> getAllCompanies() {
        return companyService.findAll();
    }

    // ID’ye göre şirket getir
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
        Optional<Company> companyOpt = companyService.findById(id);
        return companyOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // İsimle şirket getir
    @GetMapping("/name/{name}")
    public ResponseEntity<Company> getCompanyByName(@PathVariable String name) {
        Optional<Company> companyOpt = companyService.findByName(name);
        return companyOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Şirket oluştur
    @PostMapping
    public Company createCompany(@RequestBody Company company) {
        return companyService.save(company);
    }

    // Şirket güncelle
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable Long id, @RequestBody Company company) {
        Optional<Company> existingCompany = companyService.findById(id);
        if (!existingCompany.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        company.setId(id);
        Company updated = companyService.save(company);
        return ResponseEntity.ok(updated);
    }

    // Şirket sil
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        Optional<Company> existing = companyService.findById(id);
        if (existing.isPresent()) {
            companyService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Şirket ismine göre varlık kontrolü
    @GetMapping("/exists/{name}")
    public boolean existsByName(@PathVariable String name) {
        return companyService.existsByName(name);
    }
}
