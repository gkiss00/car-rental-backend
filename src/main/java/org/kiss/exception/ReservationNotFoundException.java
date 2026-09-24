package org.kiss.exception;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(String id) {
        super("No reservation found with id " + id);
    }
}
