package com.microsoft.xbox.idp.telemetry.utc.model;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;

import com.microsoft.aad.adal.AuthenticationConstants.Broker;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.XboxLiveAppConfig;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;
import com.microsoft.xbox.idp.telemetry.utc.CommonData;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class UTCCommonDataModel {
    static final String DEFAULTSANDBOX = "RETAIL";
    static final String DEFAULTSERVICES = "none";
    static final String EVENTVERSION = "1.1";
    static final String UNKNOWNAPP = "UNKNOWN";
    static final String UNKNOWNUSER = "0";
    static UTCAccessibilityInfoModel accessibilityInfo = null;
    static String appName = UNKNOWNAPP;
    static UUID applicationSession = null;
    static String deviceModel = null;
    static NetworkType netType = NetworkType.UNKNOWN;
    static String osLocale = null;
    static String userId = "0";

    private enum NetworkType {
        UNKNOWN(0),
        WIFI(1),
        CELLULAR(2),
        WIRED(3);

        private int value;

        public int getValue() {
            return this.value;
        }

        public void setValue(int value2) {
            this.value = value2;
        }

        private NetworkType(int val) {
            this.value = 0;
            setValue(val);
        }
    }

    public static CommonData getCommonData(int partCVersion) {
        return getCommonData(partCVersion, new UTCAdditionalInfoModel());
    }

    public static CommonData getCommonData(int partCVersion, UTCAdditionalInfoModel additionalInfo) {
        CommonData common = new CommonData();
        common.setEventVersion(String.format("%s.%s", new Object[]{EVENTVERSION, Integer.valueOf(partCVersion)}));
        common.setDeviceModel(getDeviceModel());
        common.setXsapiVersion(Broker.CHALLENGE_TLS_INCAPABLE_VERSION);
        common.setAppName(getAppName());
        common.setClientLanguage(getDeviceLocale());
        common.setNetwork(getNetworkConnection().getValue());
        common.setSandboxId(getSandboxId());
        common.setAppSessionId(getAppSessionId());
        common.setUserId(getUserId());
        UTCAdditionalInfoModel info = additionalInfo;
        if (info == null) {
            info = new UTCAdditionalInfoModel();
        }
        common.setAdditionalInfo(info.toJson());
        common.setAccessibilityInfo(getAccessibilityInfo().toJson());
        common.setTitleDeviceId(Interop.getTitleDeviceId());
        common.setTitleSessionId(Interop.getTitleSessionId());
        return common;
    }

    public static String getUserId() {
        if (userId == null) {
            return "0";
        }
        return userId;
    }

    public static void setUserId(String userId2) {
        if (userId2 != null) {
            userId = "x:" + userId2;
        }
    }

    private static String getAppName() {
        try {
            Context ctx = Interop.getApplicationContext();
            if (appName == UNKNOWNAPP && ctx != null) {
                appName = ctx.getApplicationInfo().packageName;
            }
        } catch (Exception ex) {
            UTCLog.log(ex.getMessage(), new Object[0]);
            appName = UNKNOWNAPP;
        }
        return appName;
    }

    private static String getDeviceModel() {
        if (deviceModel == null) {
            String unknown = UNKNOWNAPP;
            String model = Build.MODEL;
            deviceModel = unknown;
            if (model != null && !model.isEmpty()) {
                deviceModel = removePipes(model);
            }
        }
        return deviceModel;
    }

    private static String getDeviceLocale() {
        if (osLocale == null) {
            try {
                Locale deviceLocale = Locale.getDefault();
                osLocale = String.format("%s-%s", new Object[]{deviceLocale.getLanguage(), deviceLocale.getCountry()});
            } catch (Exception ex) {
                UTCLog.log(ex.getMessage(), new Object[0]);
            }
        }
        return osLocale;
    }

    private static String getSandboxId() {
        try {
            return new XboxLiveAppConfig().getSandbox();
        } catch (Exception ex) {
            UTCLog.log(ex.getMessage(), new Object[0]);
            return DEFAULTSANDBOX;
        }
    }

    private static String getAppSessionId() {
        if (applicationSession == null) {
            applicationSession = UUID.randomUUID();
        }
        return applicationSession.toString();
    }

    private static String removePipes(String parameter) {
        if (parameter != null) {
            return parameter.replace(Broker.CALLER_CACHEKEY_PREFIX, "");
        }
        return parameter;
    }

    private static com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType getNetworkConnection() {

        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.getNetworkConnection():com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType");
    }

    @SuppressLint("WrongConstant")
    private static UTCAccessibilityInfoModel getAccessibilityInfo() {
        if (accessibilityInfo != null) {
            return accessibilityInfo;
        }
        accessibilityInfo = new UTCAccessibilityInfoModel();
        try {
            Context ctx = Interop.getApplicationContext();
            if (ctx != null) {
                AccessibilityManager manager = (AccessibilityManager) ctx.getSystemService("accessibility");
                accessibilityInfo.addValue("isenabled", Boolean.valueOf(manager.isEnabled()));
                List<AccessibilityServiceInfo> serviceInfoList = manager.getEnabledAccessibilityServiceList(-1);
                String services = DEFAULTSERVICES;
                for (AccessibilityServiceInfo info : serviceInfoList) {
                    if (services.equals(DEFAULTSERVICES)) {
                        services = info.getId();
                    } else {
                        services = services + String.format(";%s", new Object[]{info.getId()});
                    }
                }
                accessibilityInfo.addValue("enabledservices", services);
            }
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
        }
        return accessibilityInfo;
    }
}
