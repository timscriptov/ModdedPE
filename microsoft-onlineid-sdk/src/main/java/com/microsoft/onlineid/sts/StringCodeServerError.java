package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.util.Locale;

public class StringCodeServerError {
    private final String _error;
    private final int _subError;

    public StringCodeServerError(String error, int subError) {
        Strings.verifyArgumentNotNullOrEmpty(error, "error");
        this._error = error;
        this._subError = subError;
    }

    public String getError() {
        return this._error;
    }

    public int getSubError() {
        return this._subError;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof StringCodeServerError)) {
            return false;
        }
        StringCodeServerError error = (StringCodeServerError) o;
        if (Objects.equals(this._error, error._error) && this._subError == error._subError) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode(this._error) + this._subError;
    }

    public String toString() {
        return String.format(Locale.US, "Server Error: %s SubError: %s", new Object[]{this._error, StsErrorCode.getFriendlyHRDescription(this._subError)});
    }
}
