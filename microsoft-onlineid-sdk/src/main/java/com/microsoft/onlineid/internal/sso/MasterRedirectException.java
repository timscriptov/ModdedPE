package com.microsoft.onlineid.internal.sso;

import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.Strings;

public class MasterRedirectException extends InternalException {
    private static final long serialVersionUID = 1;
    private final String _redirectRequestTo;

    public MasterRedirectException(String message, String redirectRequestTo) {
        super(message + ": " + redirectRequestTo);
        Strings.verifyArgumentNotNullOrEmpty(redirectRequestTo, "redirectRequestTo");
        this._redirectRequestTo = redirectRequestTo;
    }

    public String getRedirectRequestTo() {
        return this._redirectRequestTo;
    }
}
