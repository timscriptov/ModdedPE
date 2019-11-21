package com.microsoft.onlineid.internal.sso.client;

import com.microsoft.onlineid.exception.InternalException;

public class ServiceBindingException extends InternalException {
    private static final long serialVersionUID = 1;

    public ServiceBindingException(String message) {
        super(message);
    }

    public ServiceBindingException(Throwable cause) {
        super(cause);
    }

    public ServiceBindingException(String message, Throwable cause) {
        super(message, cause);
    }
}
