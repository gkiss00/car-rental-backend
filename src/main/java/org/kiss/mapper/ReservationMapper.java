package org.kiss.mapper;

import java.time.ZoneOffset;

import org.kiss.api.model.CreateReservationRequest;
import org.kiss.api.model.ReservationPage;
import org.kiss.api.model.ReservationResponse;
import org.kiss.model.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public Reservation toEntity(CreateReservationRequest request) {
        return Reservation.builder()
                .carId(request.getCarId())
                .startTime(request.getStartTime().toInstant())
                .endTime(request.getEndTime().toInstant())
                .build();
    }

    public Reservation applyUpdate(Reservation existing, CreateReservationRequest request) {
        existing.setCarId(request.getCarId());
        existing.setStartTime(request.getStartTime().toInstant());
        existing.setEndTime(request.getEndTime().toInstant());
        return existing;
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse()
                .id(reservation.getId())
                .carId(reservation.getCarId())
                .startTime(reservation.getStartTime().atOffset(ZoneOffset.UTC))
                .endTime(reservation.getEndTime().atOffset(ZoneOffset.UTC));
    }

    public ReservationPage toPage(Page<Reservation> page) {
        return new ReservationPage()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages());
    }
}
