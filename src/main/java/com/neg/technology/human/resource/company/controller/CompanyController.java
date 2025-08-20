package com.neg.technology.human.resource.company.controller;

import com.neg.technology.human.resource.company.model.request.CompanyIdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import com.neg.technology.human.resource.company.model.request.CreateCompanyRequest;
import com.neg.technology.human.resource.company.model.request.UpdateCompanyRequest;
import com.neg.technology.human.resource.company.model.response.CompanyResponse;
import com.neg.technology.human.resource.company.model.response.CompanyResponseList;
import com.neg.technology.human.resource.company.service.CompanyService;
import com.neg.technology.human.resource.company.validator.CompanyValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
    public Mono<ResponseEntity<CompanyResponse>> createCompany(@Valid @RequestBody CreateCompanyRequest request) {
        companyValidator.validateCreate(request);
        return companyService.createCompany(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Update an existing company")
    @ApiResponse(responseCode = "200", description = "Company updated successfully")
    @PostMapping("/update")
    public Mono<ResponseEntity<CompanyResponse>> updateCompany(@Valid @RequestBody UpdateCompanyRequest request) {
        companyValidator.validateUpdate(request);
        return companyService.updateCompany(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Delete a company by ID")
    @ApiResponse(responseCode = "204", description = "Company deleted successfully")
    @PostMapping("/delete")
    public Mono<ResponseEntity<Void>> deleteCompany(@Valid @RequestBody CompanyIdRequest request) {
        return companyService.deleteCompany(request)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @Operation(summary = "Get all companies")
    @ApiResponse(responseCode = "200", description = "List of companies retrieved successfully")
    @PostMapping("/getAll")
    public Mono<ResponseEntity<CompanyResponseList>> getAllCompanies() {
        return companyService.getAllCompanies()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get a company by ID")
    @ApiResponse(responseCode = "200", description = "Company found")
    @PostMapping("/getById")
    public Mono<ResponseEntity<CompanyResponse>> getCompanyById(@Valid @RequestBody CompanyIdRequest request) {
        return companyService.getCompanyById(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get a company by name")
    @ApiResponse(responseCode = "200", description = "Company found")
    @PostMapping("/getByName")
    public Mono<ResponseEntity<CompanyResponse>> getCompanyByName(@Valid @RequestBody NameRequest request) {
        return companyService.getCompanyByName(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Check if a company exists by name")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/existsByName")
    public Mono<ResponseEntity<Boolean>> existsByName(@Valid @RequestBody NameRequest request) {
        return companyService.existsByName(request)
                .map(ResponseEntity::ok);
    }
}
