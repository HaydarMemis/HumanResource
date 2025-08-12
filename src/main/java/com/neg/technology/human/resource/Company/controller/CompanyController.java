package com.neg.technology.human.resource.Company.controller;

import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import com.neg.technology.human.resource.Company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.Company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.Company.model.response.CompanyResponse;
import com.neg.technology.human.resource.Company.service.CompanyService;
import com.neg.technology.human.resource.Company.validator.CompanyValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "Create a new company")
    @ApiResponse(responseCode = "200", description = "Company created successfully")
    @PostMapping("/create")
    public ResponseEntity<CompanyResponse> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        companyValidator.validateCreate(request);
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update an existing company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company updated successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PostMapping("/update")
    public ResponseEntity<CompanyResponse> updateCompany(@Valid @RequestBody UpdateCompanyRequest request) {
        companyValidator.validateUpdate(request);
        return companyService.updateCompany(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a company by ID")
    @ApiResponse(responseCode = "204", description = "Company deleted successfully")
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCompany(@Valid @RequestBody IdRequest request) {
        companyService.deleteCompany(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all companies")
    @ApiResponse(responseCode = "200", description = "List of companies retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        List<CompanyResponse> list = companyService.getAllCompanies();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Get a company by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<CompanyResponse> getCompanyById(@Valid @RequestBody IdRequest request) {
        return companyService.getCompanyById(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a company by name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PostMapping("/getByName")
    public ResponseEntity<CompanyResponse> getCompanyByName(@Valid @RequestBody NameRequest request) {
        return companyService.getCompanyByName(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Check if a company exists by name")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/exists")
    public ResponseEntity<Boolean> companyExists(@Valid @RequestBody NameRequest request) {
        boolean exists = companyService.existsByName(request.getName());
        return ResponseEntity.ok(exists);
    }
}
