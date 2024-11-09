package com.fraud.exception;

public class FraudDetetctionServiceException extends RuntimeException{
    public FraudDetetctionServiceException(String message) {
        super(message);
    }

    public FraudDetetctionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
