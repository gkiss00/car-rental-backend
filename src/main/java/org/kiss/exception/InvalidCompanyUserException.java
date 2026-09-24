package org.kiss.exception;

public class InvalidCompanyUserException extends RuntimeException {

    public InvalidCompanyUserException(String userId) {
        super("User " + userId + " does not exist");
    }

    public InvalidCompanyUserException(String userId, String reason) {
        super("User " + userId + " " + reason);
    }
}
