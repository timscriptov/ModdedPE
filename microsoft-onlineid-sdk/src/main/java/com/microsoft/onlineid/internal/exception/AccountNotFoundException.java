package com.microsoft.onlineid.internal.exception;

import com.microsoft.onlineid.exception.AuthenticationException;

public class AccountNotFoundException extends AuthenticationException {
    private static final long serialVersionUID = 1;

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(Throwable cause) {
        super(cause);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
