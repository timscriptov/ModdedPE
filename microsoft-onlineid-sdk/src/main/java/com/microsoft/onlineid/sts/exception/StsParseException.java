package com.microsoft.onlineid.sts.exception;

import java.util.Locale;

public class StsParseException extends InvalidResponseException {
    private static final long serialVersionUID = 1;

    public StsParseException(String message, Object... args) {
        super(String.format(Locale.US, message, args));
    }

    public StsParseException(Throwable cause) {
        super(cause);
    }

    public StsParseException(String message, Throwable cause, Object... args) {
        super(String.format(Locale.US, message, args), cause);
    }
}
