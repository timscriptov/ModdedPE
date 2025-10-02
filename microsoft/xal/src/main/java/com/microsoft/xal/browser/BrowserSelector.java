package com.microsoft.xal.browser;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.util.Base64;
import androidx.browser.customtabs.CustomTabsService;
import androidx.core.os.EnvironmentCompat;
import com.microsoft.xal.logging.XalLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
public class BrowserSelector {
    private static final Map<String, String> customTabsAllowedBrowsers;

    static {
        HashMap<String, String> map = new HashMap<>();
        customTabsAllowedBrowsers = map;
        map.put("com.android.chrome", "OJGKRT0HGZNU+LGa8F7GViztV4g=");
        map.put("org.mozilla.firefox", "kg9Idqale0pqL0zK9l99Kc4m/yw=");
        map.put("com.microsoft.emmx", "P2QOJ59jvOpxCCrn6MfvotoBTK0=");
        map.put("com.sec.android.app.sbrowser", "nKUXDzgZGd/gRG/NqxixmhQ7MWM=");
    }

    public static @NotNull BrowserSelectionResult selectBrowser(Context context, boolean useInProcBrowser) throws NoSuchAlgorithmException {
        String notes;
        XalLogger xalLogger = new XalLogger("BrowserSelector");
        try {
            BrowserSelectionResult.BrowserInfo browserInfoUserDefaultBrowserInfo = userDefaultBrowserInfo(context, xalLogger);
            boolean useCustomTabs = false;
            if (useInProcBrowser) {
                notes = "inProcRequested";
            } else if (browserInfoImpliesNoUserDefault(browserInfoUserDefaultBrowserInfo)) {
                notes = "noDefault";
            } else {
                String packageName = browserInfoUserDefaultBrowserInfo.packageName;
                if (!browserSupportsCustomTabs(context, packageName)) {
                    xalLogger.Important("selectBrowser() Default browser does not support custom tabs.");
                    notes = "CTNotSupported";
                } else if (!browserAllowedForCustomTabs(context, xalLogger, packageName)) {
                    xalLogger.Important("selectBrowser() Default browser supports custom tabs, but is not allowed.");
                    notes = "CTSupportedButNotAllowed";
                } else {
                    xalLogger.Important("selectBrowser() Default browser supports custom tabs and is allowed.");
                    notes = "CTSupportedAndAllowed";
                    useCustomTabs = true;
                }
            }
            BrowserSelectionResult browserSelectionResult = new BrowserSelectionResult(browserInfoUserDefaultBrowserInfo, notes, useCustomTabs);
            xalLogger.close();
            return browserSelectionResult;
        } catch (Throwable th) {
            try {
                xalLogger.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Contract("_, _ -> new")
    private static BrowserSelectionResult.@NotNull BrowserInfo userDefaultBrowserInfo(@NotNull Context context, XalLogger xalLogger) {
        String versionName;
        ResolveInfo resolveInfoResolveActivity = context.getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://microsoft.com")), 65536);
        String packageName = resolveInfoResolveActivity == null ? null : resolveInfoResolveActivity.activityInfo.packageName;
        if (packageName == null) {
            xalLogger.Important("userDefaultBrowserInfo() No default browser resolved.");
            return new BrowserSelectionResult.BrowserInfo("none", 0, "none");
        }
        if (packageName.equals("android")) {
            xalLogger.Important("userDefaultBrowserInfo() System resolved as default browser.");
            return new BrowserSelectionResult.BrowserInfo("android", 0, "none");
        }
        int versionCode = -1;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            xalLogger.Error("userDefaultBrowserInfo() Error in getPackageInfo(): " + e);
            versionName = EnvironmentCompat.MEDIA_UNKNOWN;
        }
        xalLogger.Important("userDefaultBrowserInfo() Found " + packageName + " as user's default browser.");
        return new BrowserSelectionResult.BrowserInfo(packageName, versionCode, versionName);
    }

    @Contract(pure = true)
    private static boolean browserInfoImpliesNoUserDefault(BrowserSelectionResult.@NotNull BrowserInfo browserInfo) {
        return browserInfo.versionCode == 0 && browserInfo.versionName.equals("none");
    }

    private static boolean browserAllowedForCustomTabs(Context context, XalLogger xalLogger, String packageName) throws NoSuchAlgorithmException {
        PackageInfo packageInfo = null;
        String signatureBrowser = customTabsAllowedBrowsers.get(packageName);
        if (signatureBrowser == null) {
            return false;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 64);
        } catch (PackageManager.NameNotFoundException e) {
            xalLogger.Error("browserAllowedForCustomTabs() Error in getPackageInfo(): " + e);
        } catch (Exception e) {
            xalLogger.Error("browserAllowedForCustomTabs() Error in hashFromSignature(): " + e);
        }
        if (packageInfo == null) {
            xalLogger.Important("No package info found for package: " + packageName);
            return false;
        }
        for (Signature signature : packageInfo.signatures) {
            if (hashFromSignature(signature).equals(signatureBrowser)) {
                return true;
            }
        }
        return false;
    }

    private static boolean browserSupportsCustomTabs(@NotNull Context context, String str) {
        Iterator<ResolveInfo> it = context.getPackageManager().queryIntentServices(new Intent(CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION), 0).iterator();
        while (it.hasNext()) {
            if (it.next().serviceInfo.packageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private static String hashFromSignature(@NotNull Signature signature) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(signature.toByteArray());
        return Base64.encodeToString(messageDigest.digest(), 2);
    }
}