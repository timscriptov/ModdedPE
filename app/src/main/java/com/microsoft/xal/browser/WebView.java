package com.microsoft.xal.browser;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.appboy.Constants;
import com.mcal.mcpelauncher.R;
import com.microsoft.aad.adal.AuthenticationConstants;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongycastle.asn1.cmp.PKIFailureInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class WebView extends AppCompatActivity {
    public static final String DEFAULT_BROWSER_INFO = "webkit";
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
        if (!startUrl.isEmpty()) {
            if (!endUrl.isEmpty()) {
                ShowUrlType fromInt = ShowUrlType.fromInt(showTypeInt);
                if (fromInt == null) {
                    urlOperationFailed(operationId, false, null);
                    return;
                }

                if (startUrl.startsWith("https://sisu.xboxlive.com/client/v8/0000000048183522/view/splash.html?msa=")) {
                    try {
                        startUrl = URLDecoder.decode(startUrl.substring(74), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(context, WebView.class);
                Bundle bundle = new Bundle();
                bundle.putLong(OPERATION_ID, operationId);
                bundle.putString(START_URL, startUrl);
                bundle.putString(END_URL, endUrl);
                //bundle.putSerializable(SHOW_TYPE, fromInt);
                //bundle.putBoolean(IN_PROC_BROWSER, useInProcBrowser);
                //bundle.putLong(CANCEL_DELAY, cancelDelay);
                intent.putExtras(bundle);
                intent.setFlags(268435456);
                context.startActivity(intent);
                return;
            }
        }
        urlOperationFailed(operationId, false, null);
    }

    private static String hashFromSignature(@NotNull Signature signature) throws NoSuchAlgorithmException {
        MessageDigest instance = MessageDigest.getInstance("SHA");
        instance.update(signature.toByteArray());
        return Base64.encodeToString(instance.digest(), 2);
    }

    public void onCreate(Bundle bundle) {
        String versionName;
        super.onCreate(bundle);
        if (getIntent().getData() == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                setResult(RESULT_FAILED);
                finish();
                return;
            }
            m_operationId = extras.getLong(OPERATION_ID);
            m_cancelDelay = extras.getLong(CANCEL_DELAY, 500);
            String startUrl = extras.getString(START_URL, "");
            String endUrl = extras.getString(END_URL, "");
            String defaultBrowserPackageName = null;
            if (startUrl.isEmpty() || endUrl.isEmpty()) {
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
                setBrowserInfo("webkit-inProcRequested", 0, "none");
                startWebView(startUrl, endUrl, showUrlType);
            } else if (defaultBrowserPackageName == null || defaultBrowserPackageName.equals(Constants.HTTP_USER_AGENT_ANDROID)) {
                setBrowserInfo("webkit-noDefault", 0, "none");
                startWebView(startUrl, endUrl, showUrlType);
            } else {
                int versionCode = -1;
                try {
                    PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(defaultBrowserPackageName, 0);
                    versionCode = packageInfo.versionCode;
                    versionName = packageInfo.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    versionName = "unknown";
                }
                if (!browserSupportsCustomTabs(defaultBrowserPackageName)) {
                    setBrowserInfo(defaultBrowserPackageName + "-noCustomTabs", versionCode, versionName);
                    startWebView(startUrl, endUrl, showUrlType);
                } else if (!browserAllowedForCustomTabs(defaultBrowserPackageName)) {
                    setBrowserInfo(defaultBrowserPackageName + "-customTabsNotAllowed", versionCode, versionName);
                    startWebView(startUrl, endUrl, showUrlType);
                } else {
                    setBrowserInfo(defaultBrowserPackageName + "-customTabsAllowed", versionCode, versionName);
                    startCustomTabsInBrowser(startUrl, endUrl, showUrlType);
                }
            }
        } else {
            Intent launchIntentForPackage = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
            startActivity(launchIntentForPackage);
            finish();
        }
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri data = intent.getData();
        if (data == null) {
            finishOperation(WebResult.FAIL, null);
            return;
        }
        finishOperation(WebResult.SUCCESS, data.toString());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8053) {
            if (resultCode == -1) {
                String string = data.getExtras().getString(WebKitWebViewController.RESPONSE_KEY, "");
                if (string.isEmpty()) {
                } else {
                    finishOperation(WebResult.SUCCESS, string);
                    return;
                }
            } else if (resultCode == 0) {
                finishOperation(WebResult.CANCEL, null);
                return;
            }
            finishOperation(WebResult.FAIL, null);
        }
    }

    public void onResume() {
        super.onResume();
        if (m_cancelOperationOnResume) {
            new Handler().postDelayed(() -> {
                finishOperation(WebResult.CANCEL, null);
            }, m_cancelDelay);
        }
    }

    public void onPause() {
        super.onPause();
        m_cancelOperationOnResume = true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (m_cancelOperationOnResume) {
            finishOperation(WebResult.CANCEL, null);
            return;
        }
    }

    @SuppressLint("ResourceAsColor")
    private void startCustomTabsInBrowser(String startUrl, String endUrl, ShowUrlType showUrlType) {
        if (showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, endUrl);
            return;
        }
        m_cancelOperationOnResume = false;
        m_sharedBrowserUsed = true;
        
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(R.color.colorPrimaryDark);
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(startUrl));
    }

    private void startWebView(String startUrl, String endUrl, ShowUrlType showUrlType) {
        m_cancelOperationOnResume = false;
        m_sharedBrowserUsed = false;
        Intent intent = new Intent(getApplicationContext(), WebKitWebViewController.class);
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
            return;
        }
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
        m_browserInfo = String.format(Locale.US, "%s::%d::%s", packageName, versionCode, versionName);
    }

    private boolean browserAllowedForCustomTabs(String browserPackageName) {
        String knownSignatureHash = customTabsAllowedBrowsers.get(browserPackageName);
        if (knownSignatureHash == null) {
            return false;
        }
        try {
            @SuppressLint({"WrongConstant", "PackageManagerGetSignatures"}) PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(browserPackageName, 64);
            if (packageInfo == null) {
                return false;
            }
            for (Signature hashFromSignature : packageInfo.signatures) {
                if (hashFromSignature(hashFromSignature).equals(knownSignatureHash)) {
                    return true;
                }
            }
            return false;
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
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