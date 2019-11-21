package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class DAToken implements Serializable {
    public static final ISecurityScope Scope = new ISecurityScope() {
        public String getTarget() {
            return "http://Passport.NET/tb";
        }

        public String getPolicy() {
            return null;
        }
    };
    private static final long serialVersionUID = 1;
    private final byte[] _sessionKey;
    private final String _token;

    public DAToken(String token, byte[] sessionKey) {
        Strings.verifyArgumentNotNullOrEmpty(token, "token");
        Objects.verifyArgumentNotNull(sessionKey, "sessionKey");
        this._token = token;
        this._sessionKey = sessionKey;
    }

    public String getOneTimeSignedCredential(Date currentServerTime, String appId) {
        return new OneTimeCredentialSigner(currentServerTime, this).generateOneTimeSignedCredential(appId);
    }

    public String getToken() {
        return this._token;
    }

    public byte[] getSessionKey() {
        return this._sessionKey;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof DAToken)) {
            return false;
        }
        DAToken token = (DAToken) o;
        if (Objects.equals(this._token, token._token) && Arrays.equals(this._sessionKey, token._sessionKey)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this._token.hashCode() + Arrays.hashCode(this._sessionKey);
    }
}
