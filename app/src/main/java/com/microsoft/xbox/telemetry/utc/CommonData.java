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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * 08.10.2020
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

    public CommonData(int partCVersion) {
        eventVersion = String.format("%s.%s", new Object[]{EVENTVERSION, Integer.valueOf(partCVersion)});
    }

    private static native String get_title_telemetry_device_id();

    private static native String get_title_telemetry_session_id();

    @NotNull
    public static String getApplicationSession() {
        return applicationSession.toString();
    }

    private static String getDeviceModel() {
        String androidModel = Build.MODEL;
        if (androidModel == null || androidModel.isEmpty()) {
            return UNKNOWNAPP;
        }
        return androidModel.replace(AuthenticationConstants.Broker.CALLER_CACHEKEY_PREFIX, "");
    }

    private static String getAppName() {
        try {
            Context ctx = Interop.getApplicationContext();
            if (ctx != null) {
                return ctx.getApplicationInfo().packageName;
            }
            return UNKNOWNAPP;
        } catch (Exception ex) {
            UTCLog.log(ex.getMessage(), new Object[0]);
            return UNKNOWNAPP;
        }
    }

    @Nullable
    private static String getDeviceLocale() {
        try {
            Locale deviceLocale = Locale.getDefault();
            return String.format("%s-%s", new Object[]{deviceLocale.getLanguage(), deviceLocale.getCountry()});
        } catch (Exception ex) {
            UTCLog.log(ex.getMessage(), new Object[0]);
            return null;
        }
    }

    private static String getSandboxId() {
        try {
            return new XboxLiveAppConfig().getSandbox();
        } catch (Exception ex) {
            UTCLog.log(ex.getMessage(), new Object[0]);
            return DEFAULTSANDBOX;
        }
    }

    private static NetworkType getNetworkConnection() {
        if (netType == NetworkType.UNKNOWN && Interop.getApplicationContext() != null) {
            try {
                @SuppressLint("WrongConstant") NetworkInfo defaultNetworkInfo = ((ConnectivityManager) Interop.getApplicationContext().getSystemService("connectivity")).getActiveNetworkInfo();
                if (defaultNetworkInfo != null) {
                    if (defaultNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        switch (defaultNetworkInfo.getType()) {
                            case 0:
                            case 6:
                                netType = NetworkType.CELLULAR;
                                break;
                            case 1:
                                netType = NetworkType.WIFI;
                                break;
                            case 9:
                                netType = NetworkType.WIRED;
                                break;
                            default:
                                netType = NetworkType.UNKNOWN;
                                break;
                        }
                    }
                } else {
                    return netType;
                }
            } catch (Exception ex) {
                UTCLog.log(ex.getMessage(), new Object[0]);
                netType = NetworkType.UNKNOWN;
            }
        }
        return netType;
    }

    private static String getAccessibilityInfo() {
        try {
            Context ctx = Interop.getApplicationContext();
            if (ctx == null) {
                return "";
            }
            @SuppressLint("WrongConstant") AccessibilityManager manager = (AccessibilityManager) ctx.getSystemService("accessibility");
            HashMap<String, Object> accessibilityInfoMap = new HashMap<>();
            accessibilityInfoMap.put("isenabled", Boolean.valueOf(manager.isEnabled()));
            List<AccessibilityServiceInfo> serviceInfoList = manager.getEnabledAccessibilityServiceList(-1);
            String services = DEFAULTSERVICES;
            for (AccessibilityServiceInfo info : serviceInfoList) {
                if (services.equals(DEFAULTSERVICES)) {
                    services = info.getId();
                } else {
                    services = services + String.format(";%s", new Object[]{info.getId()});
                }
            }
            accessibilityInfoMap.put("enabledservices", services);
            return new GsonBuilder().serializeNulls().create().toJson(accessibilityInfoMap);
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
            return new GsonBuilder().serializeNulls().create().toJson(additionalInfo);
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

        private NetworkType(int val) {
            value = 0;
            setValue(val);
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value2) {
            value = value2;
        }
    }
}