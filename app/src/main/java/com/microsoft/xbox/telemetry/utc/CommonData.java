package com.microsoft.xbox.telemetry.utc;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.XboxLiveAppConfig;
import com.microsoft.xbox.telemetry.helpers.UTCLog;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * 07.01.2021
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class CommonData {
    private static final String DEFAULTSANDBOX = "RETAIL";
    private static final String DEFAULTSERVICES = "none";
    private static final String EVENTVERSION = "1.1";
    private static final String UNKNOWNAPP = "UNKNOWN";
    private static final String UNKNOWNUSER = "0";
    private static UUID applicationSession = UUID.randomUUID();
    private static NetworkType netType = getNetworkConnection();
    private static String staticAccessibilityInfo = getAccessibilityInfo();
    private static String staticAppName = getAppName();
    private static String staticDeviceModel = getDeviceModel();
    private static String staticOSLocale = getDeviceLocale();
    public HashMap<String, Object> additionalInfo = new HashMap<>();
    public String appName = staticAppName;
    public String appSessionId = getApplicationSession();
    public String clientLanguage = staticOSLocale;
    public String deviceModel = staticDeviceModel;
    public String eventVersion;
    public int network = netType.getValue();
    public String sandboxId = getSandboxId();
    public String titleDeviceId = get_title_telemetry_device_id();
    public String titleSessionId = get_title_telemetry_session_id();
    public String userId = UNKNOWNUSER;
    public String xsapiVersion = "1.0";
    private String accessibilityInfo = staticAccessibilityInfo;

    public CommonData(int i) {
        this.eventVersion = String.format("%s.%s", new Object[]{EVENTVERSION, Integer.valueOf(i)});
    }

    private static native String get_title_telemetry_device_id();

    private static native String get_title_telemetry_session_id();

    public static @NotNull String getApplicationSession() {
        return applicationSession.toString();
    }

    private static String getDeviceModel() {
        String str = Build.MODEL;
        return (str == null || str.isEmpty()) ? UNKNOWNAPP : str.replace(AuthenticationConstants.Broker.CALLER_CACHEKEY_PREFIX, "");
    }

    private static String getAppName() {
        try {
            Context applicationContext = Interop.getApplicationContext();
            if (applicationContext != null) {
                return applicationContext.getApplicationInfo().packageName;
            }
            return UNKNOWNAPP;
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
            return UNKNOWNAPP;
        }
    }

    private static @Nullable String getDeviceLocale() {
        try {
            Locale locale = Locale.getDefault();
            return String.format("%s-%s", new Object[]{locale.getLanguage(), locale.getCountry()});
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
            return null;
        }
    }

    private static String getSandboxId() {
        try {
            return new XboxLiveAppConfig().getSandbox();
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
            return DEFAULTSANDBOX;
        }
    }

    private static NetworkType getNetworkConnection() {
        if (netType == NetworkType.UNKNOWN && Interop.getApplicationContext() != null) {
            try {
                @SuppressLint("WrongConstant") NetworkInfo activeNetworkInfo = ((ConnectivityManager) Interop.getApplicationContext().getSystemService("connectivity")).getActiveNetworkInfo();
                if (activeNetworkInfo == null) {
                    return netType;
                }
                if (activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    int type = activeNetworkInfo.getType();
                    if (type != 0) {
                        if (type == 1) {
                            netType = NetworkType.WIFI;
                        } else if (type != 6) {
                            if (type != 9) {
                                netType = NetworkType.UNKNOWN;
                            } else {
                                netType = NetworkType.WIRED;
                            }
                        }
                    }
                    netType = NetworkType.CELLULAR;
                }
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                netType = NetworkType.UNKNOWN;
            }
        }
        return netType;
    }

    private static String getAccessibilityInfo() {
        try {
            Context applicationContext = Interop.getApplicationContext();
            if (applicationContext == null) {
                return "";
            }
            @SuppressLint("WrongConstant") AccessibilityManager accessibilityManager = (AccessibilityManager) applicationContext.getSystemService("accessibility");
            HashMap hashMap = new HashMap();
            hashMap.put("isenabled", Boolean.valueOf(accessibilityManager.isEnabled()));
            String str = DEFAULTSERVICES;
            for (AccessibilityServiceInfo next : accessibilityManager.getEnabledAccessibilityServiceList(-1)) {
                if (str.equals(DEFAULTSERVICES)) {
                    str = next.getId();
                } else {
                    str = str + String.format(";%s", new Object[]{next.getId()});
                }
            }
            hashMap.put("enabledservices", str);
            return new GsonBuilder().serializeNulls().create().toJson(hashMap);
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
            return "";
        }
    }

    public String ToJson() {
        try {
            return new GsonBuilder().serializeNulls().create().toJson(this);
        } catch (JsonIOException e) {
            UTCLog.log("UTCJsonSerializer", "Error in json serialization" + e.toString());
            return "";
        }
    }

    public String GetAdditionalInfoString() {
        try {
            return new GsonBuilder().serializeNulls().create().toJson(this.additionalInfo);
        } catch (JsonIOException e) {
            UTCLog.log("UTCJsonSerializer", "Error in json serialization" + e.toString());
            return null;
        }
    }

    private enum NetworkType {
        UNKNOWN(0),
        WIFI(1),
        CELLULAR(2),
        WIRED(3);

        private int value;

        private NetworkType(int i) {
            this.value = 0;
            setValue(i);
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(int i) {
            this.value = i;
        }
    }
}
