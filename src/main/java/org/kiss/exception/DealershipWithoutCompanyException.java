package org.kiss.exception;

public class DealershipWithoutCompanyException extends RuntimeException {

    public DealershipWithoutCompanyException() {
        super("Your account is not associated with a company");
    }
}
