package com.microsoft.onlineid.internal.ui;

import java.util.HashMap;
import java.util.Map;

public class PropertyBag {
    private final Map<Key, String> _values = new HashMap();

    public enum Key {
        CID,
        DAToken,
        DASessionKey,
        ErrorCode,
        ErrorString,
        ExtendedErrorString,
        ErrorURL,
        Password,
        PUID,
        STSInlineFlowToken,
        Username,
        PfUsernames,
        PfFirstName,
        PfLastName,
        PfDeviceEmail,
        PfPhone,
        PfCountryCode,
        SmsCode,
        IsSignUp,
        SigninName,
        TelemetryAppVersion,
        TelemetryDeviceYearClass,
        TelemetryIsRequestorMaster,
        TelemetryNetworkType,
        TelemetryPrecaching,
        TelemetryResourceBundleHits,
        TelemetryResourceBundleMisses,
        TelemetryResourceBundleVersion
    }

    public String get(Key key) {
        return (String) this._values.get(key);
    }

    public void set(Key key, String value) {
        this._values.put(key, value);
    }

    public void remove(Key key) {
        this._values.remove(key);
    }
}
