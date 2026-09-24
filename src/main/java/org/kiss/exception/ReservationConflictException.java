package org.kiss.exception;

public class ReservationConflictException extends RuntimeException {

    public ReservationConflictException(String carId) {
        super("Car " + carId + " already has a reservation that overlaps, or is within 2 hours of, the requested time window");
    }
}
