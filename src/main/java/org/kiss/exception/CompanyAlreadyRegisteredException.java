package org.kiss.exception;

public class CompanyAlreadyRegisteredException extends RuntimeException {

    public CompanyAlreadyRegisteredException(String registrationNumber) {
        super("A company with registration number " + registrationNumber + " is already registered");
    }
}
