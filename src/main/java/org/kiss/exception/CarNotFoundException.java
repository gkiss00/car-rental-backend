package org.kiss.exception;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(String id) {
        super("No car found with id " + id);
    }
}
