package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import java.io.Serializable;

public class DeviceIdentity implements Serializable {
    private static final long serialVersionUID = 1;
    private final DeviceCredentials _credentials;
    private final String _puid;
    private DAToken _token;

    public DeviceIdentity(DeviceCredentials credentials, String puid, DAToken token) {
        if (credentials == null || puid == null) {
            throw new IllegalArgumentException("credentials and puid must not be null.");
        }
        this._credentials = credentials;
        this._puid = puid;
        this._token = token;
    }

    public DeviceCredentials getCredentials() {
        return this._credentials;
    }

    public String getPuid() {
        return this._puid;
    }

    public DAToken getDAToken() {
        return this._token;
    }

    void setDAToken(DAToken token) {
        this._token = token;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof DeviceIdentity)) {
            return false;
        }
        DeviceIdentity id = (DeviceIdentity) o;
        if (Objects.equals(this._credentials, id._credentials) && Objects.equals(this._puid, id._puid) && Objects.equals(this._token, id._token)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this._credentials.hashCode() + this._puid.hashCode()) + Objects.hashCode(this._token);
    }
}
