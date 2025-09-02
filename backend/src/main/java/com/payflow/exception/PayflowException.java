package com.payflow.exception;

public class PayflowException extends RuntimeException {
    public PayflowException(String message) {
        super(message);
    }

    public PayflowException(String message, Throwable cause) {
        super(message, cause);
    }
}