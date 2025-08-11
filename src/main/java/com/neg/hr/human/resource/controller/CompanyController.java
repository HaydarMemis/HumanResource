package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.utilities.IdRequest;
import com.neg.hr.human.resource.dto.utilities.NameRequest;
import com.neg.hr.human.resource.dto.create.CreateCompanyRequestDTO;
import com.neg.hr.human.resource.dto.entity.CompanyEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateCompanyRequestDTO;
import com.neg.hr.human.resource.entity.Company;
import com.neg.hr.human.resource.mapper.CompanyMapper;
import com.neg.hr.human.resource.service.CompanyService;
import com.neg.hr.human.resource.validator.CompanyValidator;
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

    public CompanyController(CompanyService companyService,
                             CompanyValidator companyValidator) {
        this.companyService = companyService;
        this.companyValidator = companyValidator;
    }

    @Operation(summary = "Get all companies", description = "Retrieve a list of all companies")
    @ApiResponse(responseCode = "200", description = "List of companies retrieved successfully")
    @PostMapping("/getAll")
    public ResponseEntity<List<CompanyEntityDTO>> getAllCompanies() {
        List<CompanyEntityDTO> companies = companyService.findAll().stream()
                .map(CompanyMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(companies);
    }

    @Operation(summary = "Get company by ID", description = "Retrieve a company by its unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PostMapping("/getById")
    public ResponseEntity<CompanyEntityDTO> getCompanyById(
            @Parameter(description = "ID of the company to be retrieved", required = true)
            @Valid @RequestBody IdRequest request) {
        return companyService.findById(request.getId())
                .map(company -> ResponseEntity.ok(CompanyMapper.toDTO(company)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get company by name", description = "Retrieve a company by its name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company found"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PostMapping("/getByName")
    public ResponseEntity<CompanyEntityDTO> getCompanyByName(
            @Parameter(description = "Name of the company to be retrieved", required = true)
            @Valid @RequestBody NameRequest request) {
        return companyService.findByName(request.getName())
                .map(company -> ResponseEntity.ok(CompanyMapper.toDTO(company)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create new company", description = "Create a new company record")
    @ApiResponse(responseCode = "200", description = "Company created successfully")
    @PostMapping("/create")
    public ResponseEntity<CompanyEntityDTO> createCompany(
            @Parameter(description = "Company data for creation", required = true)
            @Valid @RequestBody CreateCompanyRequestDTO dto) {
        companyValidator.validateCreate(dto);
        Company company = CompanyMapper.toEntity(dto);
        Company saved = companyService.save(company);
        return ResponseEntity.ok(CompanyMapper.toDTO(saved));
    }

    @Operation(summary = "Update existing company", description = "Update details of an existing company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Company updated successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PostMapping("/update/{id}")
    public ResponseEntity<CompanyEntityDTO> updateCompany(
            @Parameter(description = "ID of the company to update", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated company data", required = true)
            @Valid @RequestBody UpdateCompanyRequestDTO dto) {
        companyValidator.validateUpdate(dto, id);

        Company existingCompany = companyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        CompanyMapper.updateEntity(existingCompany, dto);
        Company updated = companyService.update(id, existingCompany);

        return ResponseEntity.ok(CompanyMapper.toDTO(updated));
    }

    @Operation(summary = "Delete company", description = "Delete a company by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Company deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Company not found")
    })
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteCompany(
            @Parameter(description = "ID of the company to delete", required = true)
            @Valid @RequestBody IdRequest request) {
        companyService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if company exists by name", description = "Returns true if a company with the given name exists")
    @ApiResponse(responseCode = "200", description = "Existence check completed")
    @PostMapping("/exists")
    public ResponseEntity<Boolean> companyExists(
            @Parameter(description = "Company name to check", required = true)
            @Valid @RequestBody NameRequest request) {
        boolean exists = companyService.existsByName(request.getName());
        return ResponseEntity.ok(exists);
    }
}
