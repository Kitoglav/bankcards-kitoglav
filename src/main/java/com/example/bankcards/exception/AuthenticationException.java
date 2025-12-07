package com.example.bankcards.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(final String message) {
        super(message);
    }

    public static class InvalidToken extends AuthenticationException {
        public InvalidToken(final String message) {
            super(message);
        }
    }
    public static class NotAuthenticated extends AuthenticationException {
        public NotAuthenticated() {
            super("This operation requires user authentication");
        }
    }
}
