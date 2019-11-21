package com.microsoft.onlineid.sts.exception;

import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.StsErrorCode;

public class StsException extends InternalException {
    private static final long serialVersionUID = 1;
    private final StsError _stsError;

    public StsException(String message, StsError error) {
        super(message + ": " + error.getMessage());
        Objects.verifyArgumentNotNull(error, "error");
        this._stsError = error;
    }

    public StsErrorCode getCode() {
        return this._stsError.getCode();
    }

    public StsError getError() {
        return this._stsError;
    }
}
