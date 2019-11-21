package com.microsoft.onlineid.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import com.microsoft.onlineid.internal.ui.ProgressView;
import com.microsoft.onlineid.sdk.R;

public class NetworkConnectivity {

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType = new int[NetworkType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.None.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.WiFi.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Mobile2G.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Mobile3G.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Mobile4G.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Ethernet.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Bluetooth.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Unknown.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public enum NetworkType {
        None,
        WiFi,
        Ethernet,
        Bluetooth,
        Mobile2G,
        Mobile3G,
        Mobile4G,
        Unknown
    }

    public static boolean hasInternetConnectivity(Context applicationContext) {
        NetworkInfo networkInfo = getActiveNetworkInfo(applicationContext);
        return networkInfo != null && networkInfo.isConnected();
    }

    @TargetApi(17)
    public static boolean isAirplaneModeOn(Context applicationContext) {
        if (VERSION.SDK_INT < 17) {
            return System.getInt(applicationContext.getContentResolver(), "airplane_mode_on", 0) != 0;
        } else {
            if (Global.getInt(applicationContext.getContentResolver(), "airplane_mode_on", 0) == 0) {
                return false;
            }
            return true;
        }
    }

    private static NetworkType getNetworkType(Context applicationContext) {
        NetworkInfo network = getActiveNetworkInfo(applicationContext);
        if (network == null || !network.isConnected()) {
            return NetworkType.None;
        }
        switch (network.getType()) {
            case R.styleable.StyledTextView_font /*0*/:
            case ApiResult.ResultUINeeded /*2*/:
            case 3:
            case 4:
            case ProgressView.NumberOfDots /*5*/:
                return getMobileNetworkType(applicationContext);
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                return NetworkType.WiFi;
            case 7:
                return NetworkType.Bluetooth;
            case 9:
                return NetworkType.Ethernet;
            default:
                return NetworkType.Unknown;
        }
    }

    private static NetworkType getMobileNetworkType(Context applicationContext) {
        switch (getTelephonyManager(applicationContext).getNetworkType()) {
            case ApiResult.ResultUINeeded /*2*/:
            case 3:
            case 4:
            case 7:
            case 11:
            case 14:
                return NetworkType.Mobile2G;
            case ProgressView.NumberOfDots /*5*/:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 15:
                return NetworkType.Mobile3G;
            default:
                return NetworkType.Mobile4G;
        }
    }

    public static String getNetworkTypeForAnalytics(Context applicationContext) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[getNetworkType(applicationContext).ordinal()]) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                return "Not connected";
            case ApiResult.ResultUINeeded /*2*/:
                return "WiFi";
            case 3:
            case 4:
            case ProgressView.NumberOfDots /*5*/:
                return "Mobile";
            case 6:
                return "Ethernet";
            case 7:
                return "Bluetooth";
            default:
                return "Unknown";
        }
    }

    public static String getNetworkTypeForServerTelemetry(Context applicationContext) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[getNetworkType(applicationContext).ordinal()]) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                return "NONE";
            case ApiResult.ResultUINeeded /*2*/:
                return "WIFI";
            case 3:
                return "2G";
            case 4:
                return "3G";
            case ProgressView.NumberOfDots /*5*/:
                return "4G";
            default:
                return "UNKNOWN";
        }
    }

    private static NetworkInfo getActiveNetworkInfo(Context applicationContext) {
        return ((ConnectivityManager) applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
    }

    private static TelephonyManager getTelephonyManager(Context applicationContext) {
        return (TelephonyManager) applicationContext.getSystemService("phone");
    }
}
