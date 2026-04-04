package com.sky.tenant.exception;

public class DuplicateBusinessException extends RuntimeException {
    public DuplicateBusinessException(String message) {
        super(message);
    }

    public DuplicateBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}