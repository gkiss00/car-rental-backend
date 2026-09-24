package org.kiss.exception;

public class CarTypeNotFoundException extends RuntimeException {

    public CarTypeNotFoundException(String id) {
        super("No car type found with id " + id);
    }
}
