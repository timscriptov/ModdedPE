package com.microsoft.onlineid;

import android.os.Bundle;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.sts.ServerConfig;

public abstract class RequestOptions<B extends RequestOptions<B>> {
    protected final Bundle _values;

    protected RequestOptions() {
        this(new Bundle());
    }

    protected RequestOptions(Bundle bundle) {
        Objects.verifyArgumentNotNull(bundle, "bundle");
        this._values = bundle;
    }

    public Bundle asBundle() {
        return this._values;
    }

    public B set(String key, String value) {
        if (value != null) {
            this._values.putString(key, value);
        } else {
            this._values.remove(key);
        }
        return this;
    }

    public String get(String key) {
        return this._values.getString(key);
    }

    public String getPrefillUsername() {
        return this._values.getString(AppProperties.UsernameKey);
    }

    public B setPrefillUsername(String username) {
        this._values.putString(AppProperties.UsernameKey, username);
        return this;
    }

    public B setUnauthenticatedSessionId(String uaid) {
        this._values.putString(AppProperties.UnauthenticatedSessionIdKey, uaid);
        return this;
    }

    public String getUnauthenticatedSessionId() {
        return this._values.getString(AppProperties.UnauthenticatedSessionIdKey);
    }

    public B setFlightConfiguration(String flightConfiguration) {
        this._values.putString(AppProperties.ClientFlightKey, flightConfiguration);
        return this;
    }

    public String getFlightConfiguration() {
        return this._values.getString(AppProperties.ClientFlightKey);
    }

    public B setWasPrecachingEnabled(boolean precachingEnabled) {
        set(AppProperties.ClientWebTelemetryPrecachingEnabledKey, precachingEnabled ? ServerConfig.DefaultConfigVersion : null);
        return this;
    }

    public boolean getWasPrecachingEnabled() {
        return ServerConfig.DefaultConfigVersion.equals(this._values.getString(AppProperties.ClientWebTelemetryPrecachingEnabledKey));
    }
}
