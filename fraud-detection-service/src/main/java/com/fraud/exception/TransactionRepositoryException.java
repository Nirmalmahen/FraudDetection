package com.fraud.exception;

public class TransactionRepositoryException extends RuntimeException{
    public TransactionRepositoryException(String message) {
        super(message);
    }

    public TransactionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
