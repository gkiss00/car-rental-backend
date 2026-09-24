package org.kiss.exception;

public class CarTypeAlreadyExistsException extends RuntimeException {

    public CarTypeAlreadyExistsException(String make, String model) {
        super("A car type already exists for make " + make + " and model " + model);
    }
}
