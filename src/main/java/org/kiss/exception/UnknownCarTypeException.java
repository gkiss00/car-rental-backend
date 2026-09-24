package org.kiss.exception;

public class UnknownCarTypeException extends RuntimeException {

    public UnknownCarTypeException(String make, String model) {
        super("No car type registered for make " + make + " and model " + model);
    }
}
