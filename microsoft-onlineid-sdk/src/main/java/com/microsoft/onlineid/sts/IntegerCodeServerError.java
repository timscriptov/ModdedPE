package com.microsoft.onlineid.sts;

import java.util.Locale;

public class IntegerCodeServerError {
    private final int _error;
    private final String _message;
    private final int _subError;

    public IntegerCodeServerError(int error, int subError, String message) {
        this._error = error;
        this._subError = subError;
        this._message = message;
    }

    public IntegerCodeServerError(int error) {
        this(error, 0, null);
    }

    public IntegerCodeServerError(int error, int subError) {
        this(error, subError, null);
    }

    public int getError() {
        return this._error;
    }

    public int getSubError() {
        return this._subError;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof IntegerCodeServerError)) {
            return false;
        }
        IntegerCodeServerError error = (IntegerCodeServerError) o;
        if (this._error == error._error && this._subError == error._subError) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this._error + this._subError;
    }

    public String toString() {
        return String.format(Locale.US, "Server Error: %s SubError: %s Message: %s", new Object[]{StsErrorCode.getFriendlyHRDescription(this._error), StsErrorCode.getFriendlyHRDescription(this._subError), this._message});
    }
}
