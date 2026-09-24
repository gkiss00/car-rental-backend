package org.kiss.exception;

public class CarAlreadyRegisteredException extends RuntimeException {

    public CarAlreadyRegisteredException(String vin) {
        super("A car with VIN " + vin + " is already registered");
    }
}
