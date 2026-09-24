package org.kiss.exception;

public class InsuranceAlreadyExistsException extends RuntimeException {

    public InsuranceAlreadyExistsException(String name) {
        super("An insurance with name " + name + " already exists");
    }
}
