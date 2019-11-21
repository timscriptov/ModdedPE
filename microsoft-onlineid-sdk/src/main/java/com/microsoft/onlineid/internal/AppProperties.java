package com.microsoft.onlineid.internal;

import android.os.Bundle;
import android.text.TextUtils;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class AppProperties {
    public static final String BooleanTrueValue = "1";
    public static final String ClientFlightKey = "client_flight";
    public static final String ClientIdKey = "client_id";
    public static final String ClientWebTelemetryPrecachingEnabledKey = "client_web_telemetry_precaching_enabled";
    public static final String ClientWebTelemetryRequestedKey = "client_web_telemetry_requested";
    public static final String CobrandIdKey = "cobrandid";
    private static Set<String> LegacyQueryStringKeys = new HashSet(Arrays.asList(new String[]{ClientIdKey, ClientFlightKey, CobrandIdKey, "email", PaginatedSignInKey, "phone", PreferredMembernameTypeKey, NoPasswordKey, UnauthenticatedSessionIdKey, UsernameKey}));
    public static final String NoPasswordKey = "nopa";
    public static final String PaginatedSignInKey = "psi";
    public static final String PreferredMembernameTypeKey = "fl";
    public static final String UnauthenticatedSessionIdKey = "uaid";
    public static final String UsernameKey = "username";
    private final Bundle _values;

    public AppProperties() {
        this(null);
    }

    public AppProperties(Bundle bundle) {
        this._values = bundle == null ? new Bundle() : new Bundle(bundle);
    }

    public void set(String key, String value) {
        this._values.putString(key, value);
    }

    public String get(String key) {
        return this._values.getString(key);
    }

    public void remove(String key) {
        this._values.remove(key);
    }

    public boolean is(String key) {
        return BooleanTrueValue.equals(get(key));
    }

    public Bundle toBundle() {
        return new Bundle(this._values);
    }

    public Map<String, String> getServerValues() {
        Map<String, String> serverValues = new HashMap();
        for (String key : this._values.keySet()) {
            String value = this._values.getString(key);
            boolean isClientOnly = (!key.startsWith("client_") || key.equals(ClientIdKey) || key.equals(ClientFlightKey)) ? false : true;
            if (!(isClientOnly || value == null)) {
                serverValues.put(key, value);
            }
        }
        return serverValues;
    }

    public Map<String, String> getServerQueryStringValues() {
        Map<String, String> queryStringValues = new HashMap();
        for (Entry<String, String> entry : getServerValues().entrySet()) {
            if (LegacyQueryStringKeys.contains(entry.getKey())) {
                queryStringValues.put(entry.getKey(), entry.getValue());
            }
        }
        return queryStringValues;
    }

    public void setLegacyParameters(Bundle requestParameters) {
        setLegacyStringValue(CobrandIdKey, requestParameters, BundleMarshaller.CobrandingIdKey);
        setLegacyStringValue(PreferredMembernameTypeKey, requestParameters, BundleMarshaller.PreferredMembernameTypeKey);
        setLegacyBooleanValue(ClientWebTelemetryRequestedKey, requestParameters, BundleMarshaller.WebFlowTelemetryRequestedKey);
        setLegacyStringValue(ClientFlightKey, requestParameters, BundleMarshaller.ClientFlightsKey);
        setLegacyStringValue(UnauthenticatedSessionIdKey, requestParameters, BundleMarshaller.UnauthenticatedSessionIdKey);
        setLegacyStringValue(UsernameKey, requestParameters, BundleMarshaller.PrefillUsernameKey);
        setLegacyBooleanValue(ClientWebTelemetryPrecachingEnabledKey, requestParameters, BundleMarshaller.WebFlowTelemetryPrecachingEnabledKey);
    }

    private void setLegacyStringValue(String appPropertiesKey, Bundle requestParameters, String legacyKey) {
        String legacyValue = requestParameters.getString(legacyKey);
        if (TextUtils.isEmpty(get(appPropertiesKey)) && !TextUtils.isEmpty(legacyValue)) {
            set(appPropertiesKey, legacyValue);
        }
    }

    private void setLegacyBooleanValue(String appPropertiesKey, Bundle requestParameters, String legacyKey) {
        boolean legacyValue = requestParameters.getBoolean(legacyKey);
        if (!is(appPropertiesKey) && legacyValue) {
            set(appPropertiesKey, BooleanTrueValue);
        }
    }
}
