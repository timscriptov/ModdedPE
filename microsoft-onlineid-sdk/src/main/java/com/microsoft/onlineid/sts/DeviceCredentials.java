package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import java.io.Serializable;

public class DeviceCredentials implements Serializable {
    private static final long serialVersionUID = 1;
    private final String _password;
    private final String _username;

    public DeviceCredentials(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("username and password must not be null.");
        }
        this._username = username;
        this._password = password;
    }

    public String getUsername() {
        return this._username;
    }

    public String getPassword() {
        return this._password;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof DeviceCredentials)) {
            return false;
        }
        DeviceCredentials creds = (DeviceCredentials) o;
        if (Objects.equals(this._username, creds._username) && Objects.equals(this._password, creds._password)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this._username.hashCode() + this._password.hashCode();
    }
}
