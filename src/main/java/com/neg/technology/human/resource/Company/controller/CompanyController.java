package com.neg.technology.human.resource.Company.controller;

import com.neg.technology.human.resource.Utility.request.CompanyIdRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.model.response.CompanyResponse;
import com.neg.technology.human.resource.Company.model.response.CompanyResponseList;
import com.neg.technology.human.resource.Company.service.CompanyService;
import com.neg.technology.human.resource.Company.validator.CompanyValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Company Controller", description = "Operations related to company management")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyValidator companyValidator;

    @Operation(summary = "Create a new company")
    @ApiResponse(responseCode = "200", description = "Company created successfully")
    @PostMapping("/create")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        companyValidator.validateCreate(request);
        return ResponseEntity.ok(companyService.createCompany(request));
    }

    @Operation(summary = "Update an existing company")
    @ApiResponse(responseCode = "200", description = "Company updated successfully")
    @PostMapping("/update")
    public ResponseEntity<CompanyResponse> updateCompany(@Valid @RequestBody UpdateCompanyRequest request) {
        companyValidator.validateUpdate(request);
        return ResponseEntity.ok(companyService.updateCompany(request));
    }

    @Operation(summary = "Delete a company by ID")
    @ApiResponse(responseCode = "204", description = "Company deleted successfully")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCompany(@Valid @RequestBody CompanyIdRequest request) {
        companyService.deleteCompany(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all companies")
    @ApiResponse(responseCode = "200", description = "List of companies retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<CompanyResponseList> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @Operation(summary = "Get a company by ID")
    @ApiResponse(responseCode = "200", description = "Company found")
    @PostMapping("/getById")
    public ResponseEntity<CompanyResponse> getCompanyById(@Valid @RequestBody CompanyIdRequest request) {
        return ResponseEntity.ok(companyService.getCompanyById(request));
    }

    @Operation(summary = "Get a company by name")
    @ApiResponse(responseCode = "200", description = "Company found")
    @PostMapping("/getByName")
    public ResponseEntity<CompanyResponse> getCompanyByName(@Valid @RequestBody NameRequest request) {
        return ResponseEntity.ok(companyService.getCompanyByName(request));
    }

    @Operation(summary = "Check if a company exists by name")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByName")
    public ResponseEntity<Boolean> existsByName(@Valid @RequestBody NameRequest request) {
        return ResponseEntity.ok(companyService.existsByName(request));
    }
}
