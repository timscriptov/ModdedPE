package com.microsoft.onlineid.internal.sso.exception;

import com.microsoft.onlineid.exception.InternalException;

public class UnsupportedClientVersionException extends InternalException {
    private static final long serialVersionUID = 1;

    public UnsupportedClientVersionException(String message) {
        super(message);
    }
}
