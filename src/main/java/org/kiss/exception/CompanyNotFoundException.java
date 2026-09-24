package org.kiss.exception;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException(String id) {
        super("No company found with id " + id);
    }
}
