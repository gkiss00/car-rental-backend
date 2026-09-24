package org.kiss.exception;

public class InvalidReservationPeriodException extends RuntimeException {

    public InvalidReservationPeriodException() {
        super("endTime must be after startTime");
    }

    public InvalidReservationPeriodException(String message) {
        super(message);
    }
}
