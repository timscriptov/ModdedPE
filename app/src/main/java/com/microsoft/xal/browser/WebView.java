package com.microsoft.xal.browser;

import android.annotation.SuppressLint;
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
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.mcal.mcpelauncher.data.Constants;
import com.microsoft.aad.adal.AuthenticationConstants;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */


public class WebView extends AppCompatActivity {
    public static final String TAG = "WebView";
    public static final String CANCEL_DELAY = "CANCEL_DELAY";
    public static final String END_URL = "END_URL";
    public static final String IN_PROC_BROWSER = "IN_PROC_BROWSER";
    public static final String OPERATION_ID = "OPERATION_ID";
    public static final int RESULT_FAILED = 8052;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    public static final int WEB_KIT_WEB_VIEW_REQUEST = 8053;
    private static final Map<String, String> customTabsAllowedBrowsers = new HashMap();

    static {
        customTabsAllowedBrowsers.put("com.android.chrome", "OJGKRT0HGZNU+LGa8F7GViztV4g=");
        customTabsAllowedBrowsers.put("org.mozilla.firefox", "kg9Idqale0pqL0zK9l99Kc4m/yw=");
        customTabsAllowedBrowsers.put("com.microsoft.emmx", "P2QOJ59jvOpxCCrn6MfvotoBTK0=");
        customTabsAllowedBrowsers.put("com.sec.android.app.sbrowser", "nKUXDzgZGd/gRG/NqxixmhQ7MWM=");
    }

    private final Lock m_lock = new ReentrantLock();
    private String m_browserInfo = null;
    private long m_cancelDelay = 500;
    private boolean m_cancelOperationOnResume = true;
    private long m_operationId = 0;
    private boolean m_sharedBrowserUsed = false;

    private static native void urlOperationCanceled(long j, boolean z, String str);

    private static native void urlOperationFailed(long j, boolean z, String str);

    private static native void urlOperationSucceeded(long j, String str, boolean z, String str2);

    @SuppressLint("WrongConstant")
    public static void showUrl(long operationId, Context context, @NotNull String startUrl, String endUrl, int showTypeInt, boolean useInProcBrowser, long cancelDelay) {
        Log.e(TAG, "JNI call received.");
        if (startUrl.isEmpty() || endUrl.isEmpty()) {
            Log.e(TAG, "Received invalid start or end URL.");
            urlOperationFailed(operationId, false, null);
        } else {
            ShowUrlType showType = ShowUrlType.fromInt(showTypeInt);
            if (showType == null) {
                Log.e(TAG, "Unrecognized show type received: " + showTypeInt);
                urlOperationFailed(operationId, false, null);
            } else {
                Intent webIntent = new Intent(context, WebView.class);
                Bundle args = new Bundle();
                args.putLong(OPERATION_ID, operationId);
                args.putString(START_URL, startUrl);
                args.putString(END_URL, endUrl);
                args.putSerializable(SHOW_TYPE, showType);
                args.putBoolean(IN_PROC_BROWSER, useInProcBrowser);
                args.putLong(CANCEL_DELAY, cancelDelay);
                webIntent.putExtras(args);
                webIntent.setFlags(268435456);
                context.startActivity(webIntent);
            }
        }
    }

    private static String hashFromSignature(@NotNull Signature signature) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA");
        digest.update(signature.toByteArray());
        return Base64.encodeToString(digest.digest(), 2);
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        if (getIntent().getData() == null) {
            Log.e(TAG, "onCreate() Called with no data.");
            Bundle args = getIntent().getExtras();
            if (args == null) {
                Log.e(TAG, "onCreate() Created with no extras. Finishing.");
                setResult(RESULT_FAILED);
                finish();
                return;
            }
            m_operationId = args.getLong(OPERATION_ID);
            m_cancelDelay = args.getLong(CANCEL_DELAY, 500);
            Log.e(TAG, "onCreate() received delay:" + m_cancelDelay);
            String startUrl = args.getString(START_URL, Constants.FLAVOR);
            String endUrl = args.getString(END_URL, Constants.FLAVOR);
            if (startUrl.isEmpty() || endUrl.isEmpty()) {
                Log.e(TAG, "onCreate() Received invalid start or end URL.");
                finishOperation(WebResult.FAIL, null);
                return;
            }
            ShowUrlType showType = (ShowUrlType) args.get(SHOW_TYPE);
            boolean useInProcBrowser = args.getBoolean(IN_PROC_BROWSER);
            if (showType == ShowUrlType.NonAuthFlow) {
                useInProcBrowser = true;
            }
            @SuppressLint("WrongConstant") ResolveInfo defaultBrowser = getApplicationContext().getPackageManager().resolveActivity(new Intent("android.intent.action.VIEW", Uri.parse(AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX)), 65536);
            String defaultBrowserPackageName = defaultBrowser == null ? null : defaultBrowser.activityInfo.packageName;
            if (useInProcBrowser) {
                Log.e(TAG, "onCreate() Operation requested in-proc. Choosing WebKit strategy.");
                setBrowserInfo("webkit-inProcRequested", 0, "none");
                startWebView(startUrl, endUrl, showType);
            } else if (defaultBrowserPackageName == null || defaultBrowserPackageName.equals("android")) {
                Log.e(TAG, "onCreate() No default browser. Choosing WebKit strategy.");
                setBrowserInfo("webkit-noDefault", 0, "none");
                startWebView(startUrl, endUrl, showType);
            } else {
                int versionCode = -1;
                String versionName = "unknown";
                try {
                    PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(defaultBrowserPackageName, 0);
                    versionCode = info.versionCode;
                    versionName = info.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "onCreate() Error in getPackageInfo(): " + e);
                }
                if (!browserSupportsCustomTabs(defaultBrowserPackageName)) {
                    Log.e(TAG, "onCreate() Default browser does not support custom tabs. Choosing WebKit strategy.");
                    setBrowserInfo(defaultBrowserPackageName + "-noCustomTabs", versionCode, versionName);
                    startWebView(startUrl, endUrl, showType);
                } else if (!browserAllowedForCustomTabs(defaultBrowserPackageName)) {
                    Log.e(TAG, "onCreate() Default browser supports custom tabs, but is not allowed. Choosing WebKit strategy.");
                    setBrowserInfo(defaultBrowserPackageName + "-customTabsNotAllowed", versionCode, versionName);
                    startWebView(startUrl, endUrl, showType);
                } else {
                    Log.e(TAG, "onCreate() Default browser supports custom tabs and is allowed. Choosing CustomTabs strategy.");
                    setBrowserInfo(defaultBrowserPackageName + "-customTabsAllowed", versionCode, versionName);
                    startCustomTabsInBrowser(defaultBrowserPackageName, startUrl, endUrl, showType);
                }
            }
        } else {
            Log.e(TAG, "onCreate() Called with data. Dropping flow and starting app's main activity.");
            Intent mainActivityIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
            startActivity(mainActivityIntent);
            finish();
        }
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent() New intent received.");
        Uri intentData = intent.getData();
        if (intentData == null) {
            Log.e(TAG, "onNewIntent() New intent received with no data.");
            finishOperation(WebResult.FAIL, (String) null);
            return;
        }
        finishOperation(WebResult.SUCCESS, intentData.toString());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult() Result received.");
        if (requestCode == 8053) {
            if (resultCode == -1) {
                String endUrl = data.getExtras().getString(WebKitWebViewController.RESPONSE_KEY, Constants.FLAVOR);
                if (endUrl.isEmpty()) {
                    Log.e(TAG, "onActivityResult() Invalid final URL received from web view.");
                } else {
                    finishOperation(WebResult.SUCCESS, endUrl);
                    return;
                }
            } else if (resultCode == 0) {
                finishOperation(WebResult.CANCEL, null);
                return;
            } else if (resultCode != 8054) {
                Log.e(TAG, "onActivityResult() Unrecognized result code received from web view:" + resultCode);
            }
            finishOperation(WebResult.FAIL, null);
            return;
        }
        Log.e(TAG, "onActivityResult() Result received from unrecognized request.");
    }

    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume() Activity resumed.");
        if (m_cancelOperationOnResume) {
            Log.e(TAG, "onResume() Starting timer to cancel operation.");
            new Handler().postDelayed(() -> {
                Log.e(TAG, "WebView.onResume() Cancelling operation.");
                finishOperation(WebResult.CANCEL, null);
            }, m_cancelDelay);
        }
    }

    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause() Activity paused.");
        m_cancelOperationOnResume = true;
    }

    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy() Activity destroyed.");
        if (m_cancelOperationOnResume) {
            Log.e(TAG, "onDestroy() Cancelling operation.");
            finishOperation(WebResult.CANCEL, null);
            return;
        }
    }

    private void startCustomTabsInBrowser(String browserPackageName, String startUrl, String endUrl, ShowUrlType showType) {
        if (showType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, endUrl);
            return;
        }
        m_cancelOperationOnResume = false;
        m_sharedBrowserUsed = true;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setData(Uri.parse(startUrl));
        customTabsIntent.intent.setPackage(browserPackageName);
        startActivity(customTabsIntent.intent);
    }

    private void startWebView(String startUrl, String endUrl, ShowUrlType showType) {
        m_cancelOperationOnResume = false;
        m_sharedBrowserUsed = false;
        Intent webIntent = new Intent(getApplicationContext(), WebKitWebViewController.class);
        Bundle webViewArgs = new Bundle();
        webViewArgs.putString(START_URL, startUrl);
        webViewArgs.putString(END_URL, endUrl);
        webViewArgs.putSerializable(SHOW_TYPE, showType);
        webIntent.putExtras(webViewArgs);
        startActivityForResult(webIntent, WEB_KIT_WEB_VIEW_REQUEST);
    }

    public void finishOperation(WebResult result, String finalUrl) {
        m_lock.lock();
        long operationId = m_operationId;
        m_operationId = 0;
        m_cancelOperationOnResume = false;
        m_lock.unlock();
        finish();
        if (operationId == 0) {
            Log.e(TAG, "finishOperation() called on completed web view.");
            return;
        }
        switch (result) {
            case SUCCESS:
                urlOperationSucceeded(operationId, finalUrl, m_sharedBrowserUsed, m_browserInfo);
                return;
            case CANCEL:
                urlOperationCanceled(operationId, m_sharedBrowserUsed, m_browserInfo);
                return;
            case FAIL:
                urlOperationFailed(operationId, m_sharedBrowserUsed, m_browserInfo);
                return;
            default:
                return;
        }
    }

    private void setBrowserInfo(String packageName, int versionCode, String versionName) {
        m_browserInfo = String.format(Locale.US, "%s::%d::%s", new Object[]{packageName, Integer.valueOf(versionCode), versionName});
        Log.e(TAG, "setBrowserInfo() Set browser info: " + this.m_browserInfo);
    }

    private boolean browserAllowedForCustomTabs(String browserPackageName) {
        String knownSignatureHash = customTabsAllowedBrowsers.get(browserPackageName);
        if (knownSignatureHash == null) {
            return false;
        }
        try {
            @SuppressLint("WrongConstant") PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(browserPackageName, 64);
            if (packageInfo == null) {
                Log.e(TAG, "No package info found for package: " + browserPackageName);
                return false;
            }
            for (Signature signature : packageInfo.signatures) {
                if (hashFromSignature(signature).equals(knownSignatureHash)) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "browserAllowedForCustomTabs() Error in getPackageInfo(): " + e);
            return false;
        } catch (NoSuchAlgorithmException e2) {
            Log.e(TAG, "browserAllowedForCustomTabs() Error in hashFromSignature(): " + e2);
            return false;
        }
    }

    private boolean browserSupportsCustomTabs(String packageName) {
        //for (ResolveInfo handler : getApplicationContext().getPackageManager().queryIntentServices(new Intent("androidx.browser.customtabs.CustomTabsService"), 0)) {
        for (ResolveInfo handler : getApplicationContext().getPackageManager().queryIntentServices(new Intent("android.support.customtabs.action.CustomTabsService"), 0)) {
            if (handler.serviceInfo.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    private enum WebResult {
        SUCCESS,
        FAIL,
        CANCEL
    }

    public enum ShowUrlType {
        Normal,
        CookieRemoval,
        CookieRemovalSkipIfSharedCredentials,
        NonAuthFlow;

        @Nullable
        @Contract(pure = true)
        public static ShowUrlType fromInt(int val) {
            switch (val) {
                case 0:
                    return Normal;
                case 1:
                    return CookieRemoval;
                case 2:
                    return CookieRemovalSkipIfSharedCredentials;
                case 3:
                    return NonAuthFlow;
                default:
                    return null;
            }
        }
    }
}
