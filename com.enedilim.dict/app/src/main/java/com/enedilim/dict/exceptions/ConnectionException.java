package com.enedilim.dict.exceptions;

/**
 * Exception for signalling connectivity issues.
 */
public class ConnectionException extends Exception {
    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
