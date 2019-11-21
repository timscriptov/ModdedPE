package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.ui.ProgressView;
import com.microsoft.onlineid.sdk.R;
import java.util.Locale;

public class StsError {
    private final StsErrorCode _code;
    private final String _logMessage;
    private final String _originalErrorMessage;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode = new int[StsErrorCode.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PPCRL_REQUEST_E_DEVICE_DA_INVALID.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PPCRL_E_DEVICE_DA_TOKEN_EXPIRED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PPCRL_REQUEST_E_FORCE_SIGNIN.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_FORCESIGNIN.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_SA_CANT_DENY_APPROVED_SESSION.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_SA_CANT_APPROVE_DENIED_SESSION.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_SA_INVALID_STATE_TRANSITION.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_SA_INVALID_OPERATION.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_TOTP_AUTHENTICATOR_ID_INVALID.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
        }
    }

    public StsError(IntegerCodeServerError error) {
        Objects.verifyArgumentNotNull(error, "error");
        this._code = StsErrorCode.convertServerError(error);
        this._originalErrorMessage = error.toString();
        this._logMessage = String.format(Locale.US, "%s error caused by server error:\n%s", new Object[]{this._code.name(), this._originalErrorMessage});
    }

    public StsError(StringCodeServerError error) {
        Objects.verifyArgumentNotNull(error, "error");
        this._code = StsErrorCode.convertServerError(error);
        this._originalErrorMessage = error.toString();
        this._logMessage = String.format(Locale.US, "%s error caused by server error:\n%s", new Object[]{this._code.name(), this._originalErrorMessage});
    }

    public StsError(StsErrorCode code) {
        Objects.verifyArgumentNotNull(code, "code");
        this._code = code;
        this._originalErrorMessage = code.name();
        this._logMessage = String.format(Locale.US, "%s error.", new Object[]{this._originalErrorMessage});
    }

    public StsErrorCode getCode() {
        return this._code;
    }

    public String getMessage() {
        return this._logMessage;
    }

    public String getOriginalErrorMessage() {
        return this._originalErrorMessage;
    }

    public boolean isRetryableDeviceDAErrorForUserAuth() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[this._code.ordinal()]) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
            case ApiResult.ResultUINeeded /*2*/:
                return true;
            default:
                return false;
        }
    }

    public boolean isRetryableDeviceDAErrorForDeviceAuth() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[this._code.ordinal()]) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
            case ApiResult.ResultUINeeded /*2*/:
            case 3:
            case 4:
                return true;
            default:
                return false;
        }
    }

    public boolean isInvalidSessionError() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[this._code.ordinal()]) {
            case ProgressView.NumberOfDots /*5*/:
            case 6:
            case 7:
            case 8:
            case 9:
                return true;
            default:
                return false;
        }
    }

    public boolean isNgcKeyNotFoundError() {
        return this._code == StsErrorCode.PP_E_NGC_LOGIN_KEY_NOT_FOUND;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o instanceof StsError) && o != null) {
            return Objects.equals(this._code, ((StsError) o)._code);
        } else if (!(o instanceof StsErrorCode) || o == null) {
            return false;
        } else {
            return Objects.equals(this._code, (StsErrorCode) o);
        }
    }

    public int hashCode() {
        return Objects.hashCode(this._code);
    }
}
