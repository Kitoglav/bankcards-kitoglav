package com.example.bankcards.exception;

public class CryptographyException extends RuntimeException {
    public CryptographyException(final String message) {
        super(message);
    }

    public CryptographyException(final String message, Throwable cause) {
        super(message, cause);
    }

    public static class Encryption extends RuntimeException {
        public Encryption(final String message) {
            super(message);
        }

        public Encryption(final String message, final Throwable cause) {
            super(message, cause);
        }
    }

    public static class Decryption extends RuntimeException {
        public Decryption(String message) {
            super(message);
        }

        public Decryption(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
