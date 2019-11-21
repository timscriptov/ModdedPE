package com.microsoft.onlineid.sts.exception;

public class CorruptedUserDATokenException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public CorruptedUserDATokenException(String message) {
        super(message);
    }

    public CorruptedUserDATokenException(Throwable cause) {
        super(cause);
    }

    public CorruptedUserDATokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
