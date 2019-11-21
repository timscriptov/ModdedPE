package com.microsoft.onlineid;

import android.os.Bundle;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.sts.ServerConfig;

public class OnlineIdConfiguration {
    private Bundle _values;

    public enum PreferredSignUpMemberNameType {
        None(null),
        EasiOnly("easi"),
        Email("easi2"),
        Outlook("wld2"),
        Telephone("phone2"),
        TelephoneOnly("phone"),
        TelephoneEvenIfBlank("phone3");
        
        private final String _qsValue;

        private PreferredSignUpMemberNameType(String qsValue) {
            this._qsValue = qsValue;
        }

        private static PreferredSignUpMemberNameType fromString(String string) {
            for (PreferredSignUpMemberNameType type : values()) {
                if (Strings.equalsIgnoreCase(string, type.toString())) {
                    return type;
                }
            }
            return None;
        }

        public String toString() {
            return this._qsValue;
        }
    }

    public OnlineIdConfiguration() {
        this(PreferredSignUpMemberNameType.None);
    }

    public OnlineIdConfiguration(PreferredSignUpMemberNameType preferredSignUpMemberNameType) {
        this._values = new Bundle();
        setPreferredSignUpMemberNameType(preferredSignUpMemberNameType);
    }

    public OnlineIdConfiguration set(String key, String value) {
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

    public Bundle asBundle() {
        return new Bundle(this._values);
    }

    public PreferredSignUpMemberNameType getPreferredSignUpMemberNameType() {
        return PreferredSignUpMemberNameType.fromString(get(AppProperties.PreferredMembernameTypeKey));
    }

    public OnlineIdConfiguration setPreferredSignUpMemberNameType(PreferredSignUpMemberNameType type) {
        set(AppProperties.PreferredMembernameTypeKey, type != null ? type.toString() : null);
        return this;
    }

    public String getCobrandingId() {
        return get(AppProperties.CobrandIdKey);
    }

    public OnlineIdConfiguration setCobrandingId(String cobrandingId) {
        set(AppProperties.CobrandIdKey, cobrandingId);
        return this;
    }

    public OnlineIdConfiguration setShouldGatherWebTelemetry(boolean requestWebTelemetry) {
        set(AppProperties.ClientWebTelemetryRequestedKey, requestWebTelemetry ? ServerConfig.DefaultConfigVersion : null);
        return this;
    }

    public boolean getShouldGatherWebTelemetry() {
        return ServerConfig.DefaultConfigVersion.equals(get(AppProperties.ClientWebTelemetryRequestedKey));
    }
}
