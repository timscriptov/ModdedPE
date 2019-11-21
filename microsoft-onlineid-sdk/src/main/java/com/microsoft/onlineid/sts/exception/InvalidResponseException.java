package com.microsoft.onlineid.sts.exception;

import com.microsoft.onlineid.exception.InternalException;

public class InvalidResponseException extends InternalException {
    private static final long serialVersionUID = 1;

    public InvalidResponseException(String message) {
        super(message);
    }

    public InvalidResponseException(Throwable cause) {
        super(cause);
    }

    public InvalidResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
