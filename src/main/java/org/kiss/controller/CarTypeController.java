package org.kiss.controller;

import org.kiss.api.CarTypesApi;
import org.kiss.api.model.CarTypeCreationResponse;
import org.kiss.api.model.CarTypePage;
import org.kiss.api.model.CarTypeRequest;
import org.kiss.service.CarTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CarTypeController implements CarTypesApi {

    private final CarTypeService carTypeService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarTypePage> listCarTypes(Integer page, Integer size) {
        return ResponseEntity.ok(carTypeService.listCarTypes(page, size));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarTypeCreationResponse> createCarType(CarTypeRequest carTypeRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carTypeService.createCarType(carTypeRequest));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCarType(String id) {
        carTypeService.deleteCarType(id);
        return ResponseEntity.noContent().build();
    }
}
