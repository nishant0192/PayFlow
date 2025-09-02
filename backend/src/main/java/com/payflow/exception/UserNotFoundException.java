package com.payflow.exception;

public class UserNotFoundException extends PayflowException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
