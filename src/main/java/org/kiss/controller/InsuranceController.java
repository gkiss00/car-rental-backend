package org.kiss.controller;

import org.kiss.api.InsurancesApi;
import org.kiss.api.model.InsurancePage;
import org.kiss.api.model.InsuranceRequest;
import org.kiss.api.model.InsuranceResponse;
import org.kiss.service.InsuranceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InsuranceController implements InsurancesApi {

    private final InsuranceService insuranceService;

    @Override
    public ResponseEntity<InsurancePage> listInsurances(Integer page, Integer size) {
        return ResponseEntity.ok(insuranceService.listInsurances(page, size));
    }

    @Override
    public ResponseEntity<InsuranceResponse> getInsuranceById(String id) {
        return ResponseEntity.ok(insuranceService.getInsuranceById(id));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsuranceResponse> createInsurance(InsuranceRequest insuranceRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(insuranceService.createInsurance(insuranceRequest));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InsuranceResponse> updateInsurance(String id, InsuranceRequest insuranceRequest) {
        return ResponseEntity.ok(insuranceService.updateInsurance(id, insuranceRequest));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteInsurance(String id) {
        insuranceService.deleteInsurance(id);
        return ResponseEntity.noContent().build();
    }
}
