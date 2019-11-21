package com.microsoft.onlineid.internal.configuration;

import android.content.Context;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.storage.Storage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Settings {
    public static final String AreTestHooksEnabled = "areTestHooksEnabled";
    public static final String HasUnlockProcedureCheckRun = "hasUnlockProcedureCheckRun";
    private static Settings Instance = null;
    public static final String IsCertificateTelemetryNeeded = "isCertificateTelemetryNeeded";
    public static final String IsLoggingEnabled = "isLoggingEnabled";
    public static final String IsPseudoLocBuild = "isPseudoLocBuild";
    public static final String IsRedactionEnabled = "isRedactionEnabled";
    public static final String ShouldCheckSsoCertificatesInDebug = "shouldCheckSsoCertificatesInDebug";
    public static final String ShowMockNotifications = "showMockNotifications";
    public static final String ShowMockNotificationsCompactStyle = "showMockNotificationsCompactStyle";
    private static final String StorageFile = "settings";
    private ConcurrentMap<String, String> _defaultSettingsMap = new ConcurrentHashMap();
    private Storage _storage;

    Settings(Context context) {
        this._storage = new Storage(context, StorageFile);
        this._defaultSettingsMap.put(AreTestHooksEnabled, String.valueOf(false));
        this._defaultSettingsMap.put(IsPseudoLocBuild, String.valueOf(false));
        this._defaultSettingsMap.put(IsLoggingEnabled, String.valueOf(true));
        this._defaultSettingsMap.put(IsRedactionEnabled, String.valueOf(true));
        this._defaultSettingsMap.put(HasUnlockProcedureCheckRun, String.valueOf(false));
        this._defaultSettingsMap.put(IsCertificateTelemetryNeeded, String.valueOf(true));
    }

    public static Settings getInstance(Context context) {
        if (Instance == null) {
            synchronized (Settings.class) {
                if (Instance == null) {
                    Instance = new Settings(context);
                }
            }
        }
        return Instance;
    }

    public String getSetting(String settingName) {
        String value = null;
        try {
            value = this._storage.readString(settingName, (String) this._defaultSettingsMap.get(settingName));
        } catch (ClassCastException e) {
            Assertion.check(false);
        }
        if (value == null) {
            return (String) this._defaultSettingsMap.get(settingName);
        }
        return value;
    }

    public static boolean isDebugBuild() {
        return false;
    }

    public boolean isSettingEnabled(String settingName) {
        return Strings.equalsIgnoreCase(getSetting(settingName), "true");
    }

    public void removeSetting(String settingName) {
        this._storage.edit().remove(settingName).apply();
    }

    public void setSetting(String settingName, String settingValue) {
        if (settingName == null) {
            throw new IllegalArgumentException("SettingName cannot be null");
        }
        this._storage.edit().writeString(settingName, settingValue).apply();
    }

    public static void resetSettings() {
        Instance = null;
    }

    void setStorage(Storage storage) {
        this._storage = storage;
    }
}
