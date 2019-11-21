package com.microsoft.onlineid.internal.sso;

import android.util.SparseArray;

public enum SsoServiceError {
    Unknown(1),
    ClientNotAuthorized(2),
    UnsupportedClientVersion(3),
    StorageException(4),
    IllegalArgumentException(5),
    AccountNotFound(6),
    NetworkException(7),
    StsException(8),
    InvalidResponseException(9),
    MasterRedirectException(10),
    ClientConfigUpdateNeededException(11);
    
    private static final SparseArray<SsoServiceError> _lookup = null;
    private int _code;

    static {
        _lookup = new SparseArray();
        SsoServiceError[] values = values();
        int length = values.length;
        int i;
        while (i < length) {
            SsoServiceError error = values[i];
            _lookup.put(error.getCode(), error);
            i++;
        }
    }

    private SsoServiceError(int code) {
        this._code = code;
    }

    public int getCode() {
        return this._code;
    }

    public static SsoServiceError get(int code) {
        return (SsoServiceError) _lookup.get(code);
    }
}
