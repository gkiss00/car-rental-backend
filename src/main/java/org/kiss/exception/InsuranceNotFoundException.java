package org.kiss.exception;

public class InsuranceNotFoundException extends RuntimeException {

    public InsuranceNotFoundException(String id) {
        super("No insurance found with id " + id);
    }
}
