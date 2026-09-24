package org.kiss.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String id) {
        super("No user found with id " + id);
    }
}
