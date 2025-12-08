package com.example.bankcards.exception;

public class TransferException extends RuntimeException {
    public TransferException(String message) {
        super(message);
    }

    public static class InsufficientBalance extends TransferException {
        public InsufficientBalance(String message) {
            super(message);
        }
    }

    public static class CardExpired extends TransferException {
        public CardExpired(String message) {
            super(message);
        }
    }

    public static class CardBlocked extends TransferException {
        public CardBlocked(String message) {
            super(message);
        }
    }

    public static class CardIsPending extends TransferException {
        public CardIsPending(String message) {
            super(message);
        }
    }
}
