package org.kiss.controller;

import org.kiss.api.CompaniesApi;
import org.kiss.api.model.CompanyPage;
import org.kiss.api.model.CompanyRegistrationRequest;
import org.kiss.api.model.CompanyResponse;
import org.kiss.api.model.CompanyUpdateRequest;
import org.kiss.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CompanyController implements CompaniesApi {

    private final CompanyService companyService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> createCompany(CompanyRegistrationRequest companyRegistrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companyService.createCompany(companyRegistrationRequest));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyPage> listCompanies(Integer page, Integer size) {
        return ResponseEntity.ok(companyService.listCompanies(page, size));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> getCompanyById(String id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> updateCompany(String id, CompanyUpdateRequest companyUpdateRequest) {
        return ResponseEntity.ok(companyService.updateCompany(id, companyUpdateRequest));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> addUserToCompany(String id, String userId) {
        return ResponseEntity.ok(companyService.addUserToCompany(id, userId));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyResponse> removeUserFromCompany(String id, String userId) {
        return ResponseEntity.ok(companyService.removeUserFromCompany(id, userId));
    }
}
