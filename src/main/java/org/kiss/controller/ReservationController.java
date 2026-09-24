package org.kiss.controller;

import org.kiss.api.ReservationsApi;
import org.kiss.api.model.CreateReservationRequest;
import org.kiss.api.model.ReservationPage;
import org.kiss.api.model.ReservationResponse;
import org.kiss.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReservationController implements ReservationsApi {

    private final ReservationService reservationService;

    @Override
    public ResponseEntity<ReservationResponse> createReservation(CreateReservationRequest createReservationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(createReservationRequest));
    }

    @Override
    public ResponseEntity<ReservationResponse> getReservationById(String id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @Override
    public ResponseEntity<ReservationResponse> updateReservation(String id, CreateReservationRequest createReservationRequest) {
        return ResponseEntity.ok(reservationService.updateReservation(id, createReservationRequest));
    }

    @Override
    public ResponseEntity<Void> deleteReservation(String id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ReservationPage> listReservationsForCar(String carId, Integer page, Integer size) {
        return ResponseEntity.ok(reservationService.listReservationsForCar(carId, page, size));
    }

    @Override
    public ResponseEntity<ReservationPage> listReservationsForCompany(String companyId, Integer page, Integer size) {
        return ResponseEntity.ok(reservationService.listReservationsForCompany(companyId, page, size));
    }
}
