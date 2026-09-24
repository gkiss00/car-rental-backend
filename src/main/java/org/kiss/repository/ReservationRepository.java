package org.kiss.repository;

import java.time.Instant;
import java.util.List;

import org.kiss.model.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReservationRepository extends MongoRepository<Reservation, String> {

    List<Reservation> findByCarId(String carId);

    Page<Reservation> findByCarId(String carId, Pageable pageable);

    Page<Reservation> findByCarIdIn(List<String> carIds, Pageable pageable);

    /**
     * Reservations whose interval could conflict with a candidate window once the buffer is
     * applied: startTime before the (buffered) window end, and endTime after the (buffered) window start.
     */
    List<Reservation> findByStartTimeLessThanAndEndTimeGreaterThan(Instant bufferedWindowEnd, Instant bufferedWindowStart);
}
