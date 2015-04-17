package com.enedilim.dict.exceptions;

/**
 * Exception for signalling connectivity issues.
 */
public class ConnectionException extends Exception {
    private static final long serialVersionUID = 1L;
    private final boolean isOnline;
    private final boolean isHostAvailable;

    public ConnectionException(boolean isOnline, boolean isHostAvailable) {
        this.isOnline = isOnline;
        this.isHostAvailable = isHostAvailable;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isHostAvailable() {
        return isHostAvailable;
    }
}
