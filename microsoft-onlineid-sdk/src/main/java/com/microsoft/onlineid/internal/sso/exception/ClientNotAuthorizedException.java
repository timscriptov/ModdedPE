package com.microsoft.onlineid.internal.sso.exception;

import com.microsoft.onlineid.exception.InternalException;

public class ClientNotAuthorizedException extends InternalException {
    private static final long serialVersionUID = 1;

    public ClientNotAuthorizedException(String message) {
        super(message);
    }
}
