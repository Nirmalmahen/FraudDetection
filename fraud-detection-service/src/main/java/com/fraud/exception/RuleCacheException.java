
package com.fraud.exception;

public class RuleCacheException extends RuntimeException{
    public RuleCacheException(String message) {
        super(message);
    }

    public RuleCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}
