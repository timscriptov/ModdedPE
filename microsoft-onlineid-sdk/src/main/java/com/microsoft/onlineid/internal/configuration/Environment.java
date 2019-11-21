package com.microsoft.onlineid.internal.configuration;

import com.microsoft.onlineid.internal.Objects;
import java.net.URL;

public class Environment {
    private final URL _configUrl;
    private final String _environmentName;

    public Environment(String name, URL configUrl) {
        this._environmentName = name;
        this._configUrl = configUrl;
    }

    public String getEnvironmentName() {
        return this._environmentName;
    }

    public URL getConfigUrl() {
        return this._configUrl;
    }

    public int hashCode() {
        return Objects.hashCode(this._environmentName) + Objects.hashCode(this._configUrl);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Environment)) {
            return false;
        }
        Environment other = (Environment) o;
        if (Objects.equals(getEnvironmentName(), other.getEnvironmentName()) && Objects.equals(getConfigUrl(), other.getConfigUrl())) {
            return true;
        }
        return false;
    }
}
