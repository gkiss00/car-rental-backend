package org.kiss.exception;

public class CompanyIdRequiredException extends RuntimeException {

    public CompanyIdRequiredException() {
        super("companyId is required when creating a USER or DEALERSHIP account");
    }

    public CompanyIdRequiredException(String context) {
        super("companyId is required " + context);
    }
}
