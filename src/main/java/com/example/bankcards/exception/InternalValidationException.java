package com.example.bankcards.exception;

public class InternalValidationException extends RuntimeException {
    public InternalValidationException(final String message) {
        super(message);
    }
}
