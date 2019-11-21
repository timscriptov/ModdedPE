package com.microsoft.onlineid.exception;

public abstract class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
