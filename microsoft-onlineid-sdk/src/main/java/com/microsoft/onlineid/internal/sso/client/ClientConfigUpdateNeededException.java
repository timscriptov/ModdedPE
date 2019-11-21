package com.microsoft.onlineid.internal.sso.client;

import com.microsoft.onlineid.exception.InternalException;

public class ClientConfigUpdateNeededException extends InternalException {
    private static final long serialVersionUID = 1;

    public ClientConfigUpdateNeededException(String message) {
        super(message);
    }
}
