package org.kiss.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.kiss.api.CarsApi;
import org.kiss.api.model.CarCategory;
import org.kiss.api.model.CarPage;
import org.kiss.api.model.CarRegistrationRequest;
import org.kiss.api.model.CarResponse;
import org.kiss.api.model.CarUpdateRequest;
import org.kiss.api.model.FuelType;
import org.kiss.api.model.TransmissionType;
import org.kiss.repository.CarSearchCriteria;
import org.kiss.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CarController implements CarsApi {

    private final CarService carService;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALERSHIP')")
    public ResponseEntity<CarResponse> registerCar(CarRegistrationRequest carRegistrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.registerCar(carRegistrationRequest));
    }

    @Override
    public ResponseEntity<CarResponse> getCarById(String id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @Override
    public ResponseEntity<CarPage> listCars(Integer page, Integer size, String companyId, String make, String model,
            CarCategory category, TransmissionType transmission, FuelType fuelType,
            Double minPrice, Double maxPrice, String city, String country,
            OffsetDateTime availableFrom, OffsetDateTime availableUntil) {
        CarSearchCriteria criteria = new CarSearchCriteria(
                companyId,
                make,
                model,
                category != null ? category.name() : null,
                transmission != null ? transmission.name() : null,
                fuelType != null ? fuelType.name() : null,
                minPrice != null ? BigDecimal.valueOf(minPrice) : null,
                maxPrice != null ? BigDecimal.valueOf(maxPrice) : null,
                city,
                country,
                availableFrom != null ? availableFrom.toInstant() : null,
                availableUntil != null ? availableUntil.toInstant() : null,
                page,
                size);
        return ResponseEntity.ok(carService.searchCars(criteria));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALERSHIP')")
    public ResponseEntity<CarResponse> updateCar(String id, CarUpdateRequest carUpdateRequest) {
        return ResponseEntity.ok(carService.updateCar(id, carUpdateRequest));
    }

    @Override
    @PreAuthorize("hasRole('DEALERSHIP')")
    public ResponseEntity<CarPage> listMyCars(Integer page, Integer size) {
        return ResponseEntity.ok(carService.listCarsForCurrentDealership(page, size));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'DEALERSHIP')")
    public ResponseEntity<Void> deleteCar(String id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
