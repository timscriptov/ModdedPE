package com.microsoft.onlineid.internal.exception;

import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.Strings;

public class UserOperationException extends AuthenticationException {
    private static final long serialVersionUID = 1;
    private final String _bodyString;
    private final String _headerString;

    public UserOperationException(String bodyMessage) {
        super(bodyMessage);
        Strings.verifyArgumentNotNullOrEmpty(bodyMessage, "bodyMessage");
        this._headerString = null;
        this._bodyString = bodyMessage;
    }

    public UserOperationException(String headerString, String bodyMessage) {
        super(headerString + " " + bodyMessage);
        Strings.verifyArgumentNotNullOrEmpty(bodyMessage, "bodyMessage");
        Strings.verifyArgumentNotNullOrEmpty(headerString, "headerString");
        this._headerString = headerString;
        this._bodyString = bodyMessage;
    }

    public UserOperationException(String bodyMessage, Throwable cause) {
        super(bodyMessage, cause);
        Strings.verifyArgumentNotNullOrEmpty(bodyMessage, "bodyMessage");
        this._headerString = null;
        this._bodyString = bodyMessage;
    }

    public UserOperationException(String headerString, String bodyMessage, Throwable cause) {
        super(headerString + " " + bodyMessage, cause);
        Strings.verifyArgumentNotNullOrEmpty(bodyMessage, "bodyMessage");
        Strings.verifyArgumentNotNullOrEmpty(headerString, "headerString");
        this._headerString = headerString;
        this._bodyString = bodyMessage;
    }

    public String getHeaderString() {
        return this._headerString;
    }

    public String getBodyMessage() {
        return this._bodyString;
    }
}
