package com.microsoft.onlineid.sts.exception;

import com.microsoft.onlineid.exception.AuthenticationException;
import java.util.Locale;

public class InlineFlowException extends AuthenticationException {
    private static final long serialVersionUID = 1;
    private final String _errorCode;
    private final String _errorUrl;
    private final String _extendedErrorString;
    private final String _message;

    public InlineFlowException(String message, String errorUrl, String errorCode, String extendedErrorString) {
        super(message);
        this._message = message;
        this._errorUrl = errorUrl;
        this._errorCode = errorCode;
        this._extendedErrorString = extendedErrorString;
    }

    public String getErrorUrl() {
        return this._errorUrl;
    }

    public String getErrorCode() {
        return this._errorCode;
    }

    public String getExtendedErrorString() {
        return this._extendedErrorString;
    }

    public String getMessage() {
        return String.format(Locale.US, "Inline flow error to be resolved at '%s': %s (code = %s, extended = %s)", new Object[]{this._errorUrl, this._message, this._errorCode, this._extendedErrorString});
    }
}
