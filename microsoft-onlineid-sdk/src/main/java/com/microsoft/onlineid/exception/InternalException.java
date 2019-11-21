package com.microsoft.onlineid.exception;

public class InternalException extends AuthenticationException {
    private static final long serialVersionUID = 1;

    public InternalException(String message) {
        super(message);
    }

    public InternalException(Throwable cause) {
        super(cause);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
