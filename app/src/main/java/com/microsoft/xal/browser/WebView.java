/*
 * Copyright (C) 2018-2021 Тимашков Иван
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
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabColorSchemeParams;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.appboy.Constants;
import com.mcal.mcpelauncher.R;
import com.microsoft.aad.adal.AuthenticationConstants;

import org.jetbrains.annotations.NotNull;
import org.spongycastle.asn1.cmp.PKIFailureInfo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

    private final Lock m_lock = new ReentrantLock();
    private long m_cancelDelay = 500;
    private boolean m_cancelOperationOnResume = true;
    private long m_operationId = 0;

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

    public void onCreate(Bundle bundle) {
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
            @SuppressLint("WrongConstant") ResolveInfo resolveActivity = getApplicationContext().getPackageManager().resolveActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX)), PKIFailureInfo.notAuthorized);
            if (resolveActivity != null) {
                defaultBrowserPackageName = resolveActivity.activityInfo.packageName;
            }
            if (useInProcBrowser) {
                startWebView(startUrl, endUrl, showUrlType);
            } else if (defaultBrowserPackageName == null || defaultBrowserPackageName.equals(Constants.HTTP_USER_AGENT_ANDROID)) {
                startWebView(startUrl, endUrl, showUrlType);
            } else {
                if (!browserSupportsCustomTabs(defaultBrowserPackageName)) {
                    startWebView(startUrl, endUrl, showUrlType);
                } else {
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
                if (!string.isEmpty()) {
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
        }
    }

    @SuppressLint("ResourceAsColor")
    private void startCustomTabsInBrowser(String startUrl, String endUrl, ShowUrlType showUrlType) {
        if (showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, endUrl);
            return;
        }
        m_cancelOperationOnResume = false;

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        CustomTabColorSchemeParams params = new CustomTabColorSchemeParams.Builder()
                .setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .build();
        intentBuilder.setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, params);
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(this, Uri.parse(startUrl));
    }

    private void startWebView(String startUrl, String endUrl, ShowUrlType showUrlType) {
        m_cancelOperationOnResume = false;
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
            urlOperationSucceeded(operationId, finalUrl, false, DEFAULT_BROWSER_INFO);
        } else if (result == 2) {
            urlOperationCanceled(operationId, false, DEFAULT_BROWSER_INFO);
        } else if (result == 3) {
            urlOperationFailed(operationId, false, DEFAULT_BROWSER_INFO);
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
}