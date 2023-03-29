/*
 * Copyright (C) 2018-2022 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.microsoft.xal.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsService;
import androidx.core.os.EnvironmentCompat;

import com.microsoft.xal.logging.XalLogger;

import org.jetbrains.annotations.Contract;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class BrowserSelector {
    private static final Map<String, String> customTabsAllowedBrowsers;

    static {
        Map<String, String> hashMap = new HashMap<>();
        customTabsAllowedBrowsers = hashMap;
        hashMap.put("com.android.chrome", "OJGKRT0HGZNU+LGa8F7GViztV4g=");
        hashMap.put("org.mozilla.firefox", "kg9Idqale0pqL0zK9l99Kc4m/yw=");
        hashMap.put("com.microsoft.emmx", "P2QOJ59jvOpxCCrn6MfvotoBTK0=");
        hashMap.put("com.sec.android.app.sbrowser", "nKUXDzgZGd/gRG/NqxixmhQ7MWM=");
    }

    @NonNull
    public static BrowserSelectionResult selectBrowser(Context context, boolean z) {
        String str;
        XalLogger xalLogger = new XalLogger("BrowserSelector");
        try {
            BrowserSelectionResult.BrowserInfo userDefaultBrowserInfo = userDefaultBrowserInfo(context, xalLogger);
            boolean z2 = false;
            if (z) {
                str = "inProcRequested";
            } else if (browserInfoImpliesNoUserDefault(userDefaultBrowserInfo)) {
                str = "noDefault";
            } else {
                String str2 = userDefaultBrowserInfo.packageName;
                if (!browserSupportsCustomTabs(context, str2)) {
                    xalLogger.Important("selectBrowser() Default browser does not support custom tabs.");
                    str = "CTNotSupported";
                } else if (!browserAllowedForCustomTabs(context, xalLogger, str2)) {
                    xalLogger.Important("selectBrowser() Default browser supports custom tabs, but is not allowed.");
                    str = "CTSupportedButNotAllowed";
                } else {
                    xalLogger.Important("selectBrowser() Default browser supports custom tabs and is allowed.");
                    str = "CTSupportedAndAllowed";
                    z2 = true;
                }
            }
            BrowserSelectionResult browserSelectionResult = new BrowserSelectionResult(userDefaultBrowserInfo, str, z2);
            xalLogger.close();
            return browserSelectionResult;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                if (th != null) {
                    try {
                        xalLogger.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                } else {
                    xalLogger.close();
                }
                throw th2;
            }
        }
    }

    @NonNull
    @Contract("_, _ -> new")
    private static BrowserSelectionResult.BrowserInfo userDefaultBrowserInfo(@NonNull Context context, XalLogger xalLogger) {
        String str;
        ResolveInfo resolveActivity = context.getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://microsoft.com")), PackageManager.MATCH_DEFAULT_ONLY);
        String str2 = resolveActivity == null ? null : resolveActivity.activityInfo.packageName;
        if (str2 == null) {
            xalLogger.Important("userDefaultBrowserInfo() No default browser resolved.");
            return new BrowserSelectionResult.BrowserInfo("none", 0, "none");
        } else if (str2.equals("android")) {
            xalLogger.Important("userDefaultBrowserInfo() System resolved as default browser.");
            return new BrowserSelectionResult.BrowserInfo("android", 0, "none");
        } else {
            int i = -1;
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str2, 0);
                i = packageInfo.versionCode;
                str = packageInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                xalLogger.Error("userDefaultBrowserInfo() Error in getPackageInfo(): " + e);
                str = EnvironmentCompat.MEDIA_UNKNOWN;
            }
            xalLogger.Important("userDefaultBrowserInfo() Found " + str2 + " as user's default browser.");
            return new BrowserSelectionResult.BrowserInfo(str2, i, str);
        }
    }

    @Contract(pure = true)
    private static boolean browserInfoImpliesNoUserDefault(@NonNull BrowserSelectionResult.BrowserInfo browserInfo) {
        return browserInfo.versionCode == 0 && browserInfo.versionName.equals("none");
    }

    @SuppressLint("PackageManagerGetSignatures")
    private static boolean browserAllowedForCustomTabs(Context context, XalLogger xalLogger, String str) {
        PackageInfo packageInfo;
        String str2 = customTabsAllowedBrowsers.get(str);
        if (str2 == null) {
            return false;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, PackageManager.GET_SIGNATURES);
            if (packageInfo == null) {
                xalLogger.Important("No package info found for package: " + str);
                return false;
            }
            for (Signature signature : packageInfo.signatures) {
                if (hashFromSignature(signature).equals(str2)) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            xalLogger.Error("browserAllowedForCustomTabs() Error in getPackageInfo(): " + e);
        } catch (NoSuchAlgorithmException e2) {
            xalLogger.Error("browserAllowedForCustomTabs() Error in hashFromSignature(): " + e2);
        }
        return false;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private static boolean browserSupportsCustomTabs(@NonNull Context context, String str) {
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentServices(new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION), 0)) {
            if (resolveInfo.serviceInfo.packageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static String hashFromSignature(@NonNull Signature signature) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(signature.toByteArray());
        return Base64.encodeToString(messageDigest.digest(), 2);
    }
}