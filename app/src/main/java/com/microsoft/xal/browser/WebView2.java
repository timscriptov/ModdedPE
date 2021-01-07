package com.microsoft.xal.browser;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import androidx.browser.customtabs.CustomTabsIntent;

import com.appboy.Constants;
import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.xal.logging.XalLogger;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongycastle.asn1.cmp.PKIFailureInfo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WebView2 extends Activity {
    public static final String CANCEL_DELAY = "CANCEL_DELAY";
    public static final String END_URL = "END_URL";
    public static final String IN_PROC_BROWSER = "IN_PROC_BROWSER";
    public static final String OPERATION_ID = "OPERATION_ID";
    public static final int RESULT_FAILED = 8052;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    public static final int WEB_KIT_WEB_VIEW_REQUEST = 8053;
    private static final Map<String, String> customTabsAllowedBrowsers;

    static {
        HashMap hashMap = new HashMap();
        customTabsAllowedBrowsers = hashMap;
        hashMap.put("com.android.chrome", "OJGKRT0HGZNU+LGa8F7GViztV4g=");
        customTabsAllowedBrowsers.put("org.mozilla.firefox", "kg9Idqale0pqL0zK9l99Kc4m/yw=");
        customTabsAllowedBrowsers.put("com.microsoft.emmx", "P2QOJ59jvOpxCCrn6MfvotoBTK0=");
        customTabsAllowedBrowsers.put("com.sec.android.app.sbrowser", "nKUXDzgZGd/gRG/NqxixmhQ7MWM=");
    }

    public final XalLogger m_logger = new XalLogger("WebView");
    private final Lock m_lock = new ReentrantLock();
    private String m_browserInfo = null;
    private long m_cancelDelay = 500;
    private boolean m_cancelOperationOnResume = true;
    private long m_operationId = 0;
    private boolean m_sharedBrowserUsed = false;

    private static native void urlOperationCanceled(long operationId, boolean sharedBrowserUsed, String browserInfo);

    private static native void urlOperationFailed(long operationId, boolean sharedBrowserUsed, String browserInfo);

    private static native void urlOperationSucceeded(long operationId, String finalUrl, boolean sharedBrowserUsed, String browserInfo);

    @SuppressLint("WrongConstant")
    public static void showUrl(long operationId, Context context, @NotNull String startUrl, String endUrl, int showTypeInt, boolean useInProcBrowser, long cancelDelay) {
        XalLogger xalLogger = new XalLogger("WebView.showUrl()");
        xalLogger.Important("JNI call received.");
        if (!startUrl.isEmpty()) {
            if (!endUrl.isEmpty()) {
                ShowUrlType fromInt = ShowUrlType.fromInt(showTypeInt);
                if (fromInt == null) {
                    xalLogger.Error("Unrecognized show type received: " + showTypeInt);
                    urlOperationFailed(operationId, false, (String) null);
                    xalLogger.close();
                    return;
                }
                Intent intent = new Intent(context, WebView2.class);
                Bundle bundle = new Bundle();
                bundle.putLong(OPERATION_ID, operationId);
                bundle.putString(START_URL, startUrl);
                bundle.putString(END_URL, endUrl);
                bundle.putSerializable(SHOW_TYPE, fromInt);
                bundle.putBoolean(IN_PROC_BROWSER, useInProcBrowser);
                bundle.putLong(CANCEL_DELAY, cancelDelay);
                intent.putExtras(bundle);
                intent.setFlags(268435456);
                context.startActivity(intent);
                xalLogger.close();
                return;
            }
        }
        xalLogger.Error("Received invalid start or end URL.");
        urlOperationFailed(operationId, false, (String) null);
        xalLogger.close();
    }

    private static String hashFromSignature(@NotNull Signature signature) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA");
        instance.update(signature.toByteArray());
        return Base64.encodeToString(instance.digest(), 2);
    }

    public void onCreate(Bundle bundle) {
        String versionName;
        m_logger.Important("onCreate()");
        super.onCreate(bundle);
        if (getIntent().getData() == null) {
            m_logger.Important("onCreate() Called with no data.");
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                m_logger.Error("onCreate() Created with no extras. Finishing.");
                m_logger.Flush();
                setResult(RESULT_FAILED);
                finish();
                return;
            }
            m_operationId = extras.getLong(OPERATION_ID);
            m_cancelDelay = extras.getLong(CANCEL_DELAY, 500);
            XalLogger xalLogger = m_logger;
            xalLogger.Information("onCreate() received delay:" + m_cancelDelay);
            String startUrl = extras.getString(START_URL, "");
            String endUrl = extras.getString(END_URL, "");
            String defaultBrowserPackageName = null;
            if (startUrl.isEmpty() || endUrl.isEmpty()) {
                this.m_logger.Error("onCreate() Received invalid start or end URL.");
                finishOperation(WebResult.FAIL, (String) null);
                return;
            }
            ShowUrlType showUrlType = (ShowUrlType) extras.get(SHOW_TYPE);
            boolean useInProcBrowser = extras.getBoolean(IN_PROC_BROWSER);
            if (showUrlType == ShowUrlType.NonAuthFlow) {
                useInProcBrowser = true;
            }
            @SuppressLint("WrongConstant") ResolveInfo resolveActivity = getApplicationContext().getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse(AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX)), PKIFailureInfo.notAuthorized);
            if (resolveActivity != null) {
                defaultBrowserPackageName = resolveActivity.activityInfo.packageName;
            }
            if (useInProcBrowser) {
                m_logger.Important("onCreate() Operation requested in-proc. Choosing WebKit strategy.");
                setBrowserInfo("webkit-inProcRequested", 0, "none");
                startWebView(startUrl, endUrl, showUrlType);
            } else if (defaultBrowserPackageName == null || defaultBrowserPackageName.equals(Constants.HTTP_USER_AGENT_ANDROID)) {
                m_logger.Important("onCreate() No default browser. Choosing WebKit strategy.");
                setBrowserInfo("webkit-noDefault", 0, "none");
                startWebView(startUrl, endUrl, showUrlType);
            } else {
                int versionCode = -1;
                try {
                    PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(defaultBrowserPackageName, 0);
                    versionCode = packageInfo.versionCode;
                    versionName = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    XalLogger xalLogger2 = m_logger;
                    xalLogger2.Error("onCreate() Error in getPackageInfo(): " + e);
                    versionName = "unknown";
                }
                if (!browserSupportsCustomTabs(defaultBrowserPackageName)) {
                    m_logger.Important("onCreate() Default browser does not support custom tabs. Choosing WebKit strategy.");
                    setBrowserInfo(defaultBrowserPackageName + "-noCustomTabs", versionCode, versionName);
                    startWebView(startUrl, endUrl, showUrlType);
                } else if (!browserAllowedForCustomTabs(defaultBrowserPackageName)) {
                    m_logger.Important("onCreate() Default browser supports custom tabs, but is not allowed. Choosing WebKit strategy.");
                    setBrowserInfo(defaultBrowserPackageName + "-customTabsNotAllowed", versionCode, versionName);
                    startWebView(startUrl, endUrl, showUrlType);
                } else {
                    m_logger.Important("onCreate() Default browser supports custom tabs and is allowed. Choosing CustomTabs strategy.");
                    setBrowserInfo(defaultBrowserPackageName + "-customTabsAllowed", versionCode, versionName);
                    startCustomTabsInBrowser(defaultBrowserPackageName, startUrl, endUrl, showUrlType);
                }
            }
        } else {
            m_logger.Warning("onCreate() Called with data. Dropping flow and starting app's main activity.");
            Intent launchIntentForPackage = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
            m_logger.Flush();
            startActivity(launchIntentForPackage);
            finish();
        }
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        m_logger.Important("onNewIntent() New intent received.");
        Uri data = intent.getData();
        if (data == null) {
            m_logger.Error("onNewIntent() New intent received with no data.");
            finishOperation(WebResult.FAIL, (String) null);
            return;
        }
        finishOperation(WebResult.SUCCESS, data.toString());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.m_logger.Important("onActivityResult() Result received.");
        if (requestCode == 8053) {
            if (resultCode == -1) {
                String string = data.getExtras().getString(WebKitWebViewController2.RESPONSE_KEY, "");
                if (string.isEmpty()) {
                    this.m_logger.Error("onActivityResult() Invalid final URL received from web view.");
                } else {
                    finishOperation(WebResult.SUCCESS, string);
                    return;
                }
            } else if (resultCode == 0) {
                finishOperation(WebResult.CANCEL, (String) null);
                return;
            } else if (resultCode != 8054) {
                XalLogger xalLogger = m_logger;
                xalLogger.Warning("onActivityResult() Unrecognized result code received from web view:" + resultCode);
            }
            finishOperation(WebResult.FAIL, (String) null);
            return;
        }
        this.m_logger.Warning("onActivityResult() Result received from unrecognized request.");
    }

    public void onResume() {
        super.onResume();
        m_logger.Important("onResume() Activity resumed.");
        if (m_cancelOperationOnResume) {
            m_logger.Important("onResume() Starting timer to cancel operation.");
            new Handler().postDelayed((Runnable) () -> {
                m_logger.Important("WebView.onResume() Cancelling operation.");
                finishOperation(WebResult.CANCEL, (String) null);
            }, m_cancelDelay);
        }
    }

    public void onPause() {
        super.onPause();
        m_logger.Important("onPause() Activity paused.");
        m_cancelOperationOnResume = true;
    }

    public void onDestroy() {
        super.onDestroy();
        m_logger.Important("onDestroy() Activity destroyed.");
        if (m_cancelOperationOnResume) {
            m_logger.Important("onDestroy() Cancelling operation.");
            finishOperation(WebResult.CANCEL, (String) null);
            return;
        }
        m_logger.Flush();
    }

    private void startCustomTabsInBrowser(String browserPackageName, String startUrl, String endUrl, ShowUrlType showUrlType) {
        if (showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, endUrl);
            return;
        }
        m_cancelOperationOnResume = false;
        m_sharedBrowserUsed = true;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        CustomTabsIntent build = builder.build();
        build.intent.setData(Uri.parse(startUrl));
        build.intent.setPackage(browserPackageName);
        startActivity(build.intent);
    }

    private void startWebView(String startUrl, String endUrl, ShowUrlType showUrlType) {
        m_cancelOperationOnResume = false;
        m_sharedBrowserUsed = false;
        Intent intent = new Intent(getApplicationContext(), WebKitWebViewController2.class);
        Bundle bundle = new Bundle();
        bundle.putString(START_URL, startUrl);
        bundle.putString(END_URL, endUrl);
        bundle.putSerializable(SHOW_TYPE, showUrlType);
        intent.putExtras(bundle);
        startActivityForResult(intent, WEB_KIT_WEB_VIEW_REQUEST);
    }

    public void finishOperation(WebResult webResult, String finalUrl) {
        m_lock.lock();
        long operationId = m_operationId;
        m_operationId = 0;
        m_cancelOperationOnResume = false;
        m_lock.unlock();
        finish();
        if (operationId == 0) {
            m_logger.Error("finishOperation() called on completed web view.");
            m_logger.Flush();
            return;
        }
        m_logger.Flush();
        int result = XalWebResult.mWebResult[webResult.ordinal()];
        if (result == 1) {
            urlOperationSucceeded(operationId, finalUrl, m_sharedBrowserUsed, m_browserInfo);
        } else if (result == 2) {
            urlOperationCanceled(operationId, m_sharedBrowserUsed, m_browserInfo);
        } else if (result == 3) {
            urlOperationFailed(operationId, m_sharedBrowserUsed, m_browserInfo);
        }
    }

    private void setBrowserInfo(String packageName, int versionCode, String versionName) {
        m_browserInfo = String.format(Locale.US, "%s::%d::%s", new Object[]{packageName, Integer.valueOf(versionCode), versionName});
        XalLogger xalLogger = m_logger;
        xalLogger.Important("setBrowserInfo() Set browser info: " + m_browserInfo);
    }

    private boolean browserAllowedForCustomTabs(String browserPackageName) {
        String knownSignatureHash = customTabsAllowedBrowsers.get(browserPackageName);
        if (knownSignatureHash == null) {
            return false;
        }
        try {
            @SuppressLint("WrongConstant") PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(browserPackageName, 64);
            if (packageInfo == null) {
                m_logger.Important("No package info found for package: " + browserPackageName);
                return false;
            }
            for (Signature hashFromSignature : packageInfo.signatures) {
                if (hashFromSignature(hashFromSignature).equals(knownSignatureHash)) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            m_logger.Error("browserAllowedForCustomTabs() Error in getPackageInfo(): " + e);
            return false;
        } catch (NoSuchAlgorithmException e2) {
            m_logger.Error("browserAllowedForCustomTabs() Error in hashFromSignature(): " + e2);
            return false;
        }
    }

    private boolean browserSupportsCustomTabs(String packageName) {
        for (ResolveInfo resolveInfo : getApplicationContext().getPackageManager().queryIntentServices(new Intent("android.support.customtabs.action.CustomTabsService"), 0)) {
            if (resolveInfo.serviceInfo.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public enum WebResult {
        SUCCESS,
        FAIL,
        CANCEL
    }

    public enum ShowUrlType {
        Normal,
        CookieRemoval,
        CookieRemovalSkipIfSharedCredentials,
        NonAuthFlow;

        @Contract(pure = true)
        public static @Nullable ShowUrlType fromInt(int val) {
            if (val == 0) {
                return Normal;
            }
            if (val == 1) {
                return CookieRemoval;
            }
            if (val == 2) {
                return CookieRemovalSkipIfSharedCredentials;
            }
            if (val != 3) {
                return null;
            }
            return NonAuthFlow;
        }
    }

    static class XalWebResult {
        static final int[] mWebResult;

        static {
            int[] iArr = new int[WebResult.values().length];
            mWebResult = iArr;
            iArr[WebResult.SUCCESS.ordinal()] = 1;
            mWebResult[WebResult.CANCEL.ordinal()] = 2;
            try {
                mWebResult[WebResult.FAIL.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
                e.printStackTrace();
            }
        }
    }
}