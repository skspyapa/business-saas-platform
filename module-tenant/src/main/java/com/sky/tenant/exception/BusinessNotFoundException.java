package com.sky.tenant.exception;

public class BusinessNotFoundException extends RuntimeException {
    public BusinessNotFoundException(String message) {
        super(message);
    }

    public BusinessNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}