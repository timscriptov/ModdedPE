package com.microsoft.onlineid.internal.sso;

public class SsoService {
    public static final String SsoServiceIntent = "com.microsoft.msa.action.SSO_SERVICE";
    private final long _firstInstallTime;
    private final String _packageName;
    private final String _sdkVersion;
    private final int _ssoVersion;

    public SsoService(String packageName, int ssoVersion, String sdkVersion) {
        this._packageName = packageName;
        this._ssoVersion = ssoVersion;
        this._sdkVersion = sdkVersion;
        this._firstInstallTime = -1;
    }

    public SsoService(String packageName, int ssoVersion, String sdkVersion, long firstInstallTime) {
        this._packageName = packageName;
        this._ssoVersion = ssoVersion;
        this._sdkVersion = sdkVersion;
        this._firstInstallTime = firstInstallTime;
    }

    public String getPackageName() {
        return this._packageName;
    }

    public int getSsoVersion() {
        return this._ssoVersion;
    }

    public String getSdkVersion() {
        return this._sdkVersion;
    }

    public long getFirstInstallTime() {
        return this._firstInstallTime;
    }

    public String toString() {
        return "[" + this._packageName + ": sso " + this._ssoVersion + ", sdk " + this._sdkVersion + "]";
    }
}
