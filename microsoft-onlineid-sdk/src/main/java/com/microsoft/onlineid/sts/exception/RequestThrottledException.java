package com.microsoft.onlineid.sts.exception;

import com.microsoft.onlineid.exception.NetworkException;

public class RequestThrottledException extends NetworkException {
    private static final long serialVersionUID = 1;

    public RequestThrottledException(String message) {
        super(message);
    }

    public RequestThrottledException(Throwable cause) {
        super(cause);
    }

    public RequestThrottledException(String message, Throwable cause) {
        super(message, cause);
    }
}
