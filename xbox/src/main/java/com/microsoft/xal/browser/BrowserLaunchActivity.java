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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
@SuppressWarnings("JavaJniMissingFunction")
public class BrowserLaunchActivity extends Activity {
    public static final String END_URL = "END_URL";
    public static final String IN_PROC_BROWSER = "IN_PROC_BROWSER";
    public static final String OPERATION_ID = "OPERATION_ID";
    public static final String REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS";
    public static final String REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES";
    public static final int RESULT_FAILED = 8052;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    public static final int WEB_KIT_WEB_VIEW_REQUEST = 8053;
    private static final String BROWSER_INFO_STATE_KEY = "BROWSER_INFO_STATE";
    private static final String CUSTOM_TABS_IN_PROGRESS_STATE_KEY = "CUSTOM_TABS_IN_PROGRESS_STATE";
    private static final String OPERATION_ID_STATE_KEY = "OPERATION_ID_STATE";
    private static final String SHARED_BROWSER_USED_STATE_KEY = "SHARED_BROWSER_USED_STATE";
    private BrowserLaunchParameters m_launchParameters = null;
    private long m_operationId = 0;
    private boolean m_customTabsInProgress = false;
    private boolean m_sharedBrowserUsed = false;
    private String m_browserInfo = null;

    private static native void checkIsLoaded();

    private static native void urlOperationCanceled(long operationId, boolean sharedBrowserUsed, String browserInfo);

    private static native void urlOperationFailed(long operationId, boolean sharedBrowserUsed, String browserInfo);

    private static native void urlOperationSucceeded(long operationId, String finalUrl, boolean sharedBrowserUsed, String browserInfo);

    public static void showUrl(long operationId, Context context, @NonNull String startUrl, String endUrl, int showTypeInt, String[] requestHeaderKeys, String[] requestHeaderValues, boolean useInProcBrowser) {
        if (!startUrl.isEmpty() && !endUrl.isEmpty()) {
            ShowUrlType fromInt = ShowUrlType.fromInt(showTypeInt);
            if (fromInt == null) {
                urlOperationFailed(operationId, false, null);
                return;
            } else if (requestHeaderKeys.length != requestHeaderValues.length) {
                urlOperationFailed(operationId, false, null);
                return;
            } else {
                Intent intent = new Intent(context, BrowserLaunchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(OPERATION_ID, operationId);
                bundle.putString(START_URL, startUrl);
                bundle.putString(END_URL, endUrl);
                bundle.putSerializable(SHOW_TYPE, fromInt);
                bundle.putStringArray(REQUEST_HEADER_KEYS, requestHeaderKeys);
                bundle.putStringArray(REQUEST_HEADER_VALUES, requestHeaderValues);
                bundle.putBoolean(IN_PROC_BROWSER, useInProcBrowser);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }
        urlOperationFailed(operationId, false, null);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (!checkNativeCodeLoaded()) {
            startActivity(getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName()));
            finish();
        } else if (bundle != null) {
            m_operationId = bundle.getLong(OPERATION_ID_STATE_KEY);
            m_customTabsInProgress = bundle.getBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY);
            m_sharedBrowserUsed = bundle.getBoolean(SHARED_BROWSER_USED_STATE_KEY);
            m_browserInfo = bundle.getString(BROWSER_INFO_STATE_KEY);
        } else if (extras != null) {
            m_operationId = extras.getLong(OPERATION_ID, 0L);
            BrowserLaunchParameters FromArgs = BrowserLaunchParameters.FromArgs(extras);
            m_launchParameters = FromArgs;
            if (FromArgs != null && m_operationId != 0) {
                return;
            }
            finishOperation(WebResult.FAIL, null);
        } else if (getIntent().getData() != null) {
            setResult(RESULT_FAILED);
            finishOperation(WebResult.FAIL, null);
        } else {
            setResult(RESULT_FAILED);
            finishOperation(WebResult.FAIL, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean z = m_customTabsInProgress;
        if (!z && m_launchParameters != null) {
            BrowserLaunchParameters browserLaunchParameters = m_launchParameters;
            m_launchParameters = null;
            startAuthSession(browserLaunchParameters);
        } else if (z) {
            m_customTabsInProgress = false;
            Uri data = getIntent().getData();
            if (data != null) {
                finishOperation(WebResult.SUCCESS, data.toString());
                return;
            }
            finishOperation(WebResult.CANCEL, null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putLong(OPERATION_ID_STATE_KEY, m_operationId);
        bundle.putBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY, m_customTabsInProgress);
        bundle.putBoolean(SHARED_BROWSER_USED_STATE_KEY, m_sharedBrowserUsed);
        bundle.putString(BROWSER_INFO_STATE_KEY, m_browserInfo);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == WEB_KIT_WEB_VIEW_REQUEST) {
            if (resultCode == -1) {
                String string = intent.getExtras().getString(WebKitWebViewController.RESPONSE_KEY, "");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isFinishing() || m_operationId == 0) {
            return;
        }
        finishOperation(WebResult.CANCEL, null);
    }

    private void startAuthSession(@NonNull BrowserLaunchParameters browserLaunchParameters) {
        BrowserSelectionResult selectBrowser = BrowserSelector.selectBrowser(getApplicationContext(), browserLaunchParameters.UseInProcBrowser);
        m_browserInfo = selectBrowser.toString();
        String packageName = selectBrowser.packageName();
        if (packageName == null) {
            startWebView(browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType, browserLaunchParameters.RequestHeaderKeys, browserLaunchParameters.RequestHeaderValues);
            return;
        }
        startCustomTabsInBrowser(packageName, browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType);
    }

    private void startCustomTabsInBrowser(String packageName, String startUrl, String endUrl, ShowUrlType showUrlType) {
        if (showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, endUrl);
            return;
        }
        m_customTabsInProgress = true;
        m_sharedBrowserUsed = true;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        CustomTabsIntent build = builder.build();
        build.intent.setData(Uri.parse(startUrl));
        build.intent.setPackage(packageName);
        startActivity(build.intent);
    }

    private void startWebView(String startUrl, String endUrl, ShowUrlType showUrlType, String[] requestHeaderKeys, String[] requestHeaderValues) {
        m_sharedBrowserUsed = false;
        Intent intent = new Intent(getApplicationContext(), WebKitWebViewController.class);
        Bundle bundle = new Bundle();
        bundle.putString(START_URL, startUrl);
        bundle.putString(END_URL, endUrl);
        bundle.putSerializable(SHOW_TYPE, showUrlType);
        bundle.putStringArray(REQUEST_HEADER_KEYS, requestHeaderKeys);
        bundle.putStringArray(REQUEST_HEADER_VALUES, requestHeaderValues);
        intent.putExtras(bundle);
        startActivityForResult(intent, WEB_KIT_WEB_VIEW_REQUEST);
    }

    private void finishOperation(WebResult webResult, String finalUrl) {
        long j = m_operationId;
        m_operationId = 0L;
        finish();
        if (j == 0) {
            return;
        }
        int result = XalWebResult.mWebResult[webResult.ordinal()];
        if (result == 1) {
            urlOperationSucceeded(j, finalUrl, m_sharedBrowserUsed, m_browserInfo);
        } else if (result == 2) {
            urlOperationCanceled(j, m_sharedBrowserUsed, m_browserInfo);
        } else if (result != 3) {
        } else {
            urlOperationFailed(j, m_sharedBrowserUsed, m_browserInfo);
        }
    }

    private boolean checkNativeCodeLoaded() {
        try {
            checkIsLoaded();
            return true;
        } catch (UnsatisfiedLinkError unused) {
            return false;
        }
    }

    public enum WebResult {
        SUCCESS,
        FAIL,
        CANCEL
    }

    public static class BrowserLaunchParameters {
        public final String EndUrl;
        public final String[] RequestHeaderKeys;
        public final String[] RequestHeaderValues;
        public final ShowUrlType ShowType;
        public final String StartUrl;
        public boolean UseInProcBrowser;

        private BrowserLaunchParameters(String startUrl, String endUrl, String[] requestHeaderKeys, String[] requestHeaderValues, ShowUrlType showUrlType, boolean useInProcBrowser) {
            StartUrl = startUrl;
            EndUrl = endUrl;
            RequestHeaderKeys = requestHeaderKeys;
            RequestHeaderValues = requestHeaderValues;
            ShowType = showUrlType;
            UseInProcBrowser = true;
        }

        @Nullable
        public static BrowserLaunchParameters FromArgs(@NonNull Bundle bundle) {
            String startUrl = bundle.getString(START_URL);
            String endUrl = bundle.getString(END_URL);
            String[] headerKeys = bundle.getStringArray(REQUEST_HEADER_KEYS);
            String[] headerValues = bundle.getStringArray(REQUEST_HEADER_VALUES);
            ShowUrlType showUrlType = (ShowUrlType) bundle.get(SHOW_TYPE);
            boolean z = bundle.getBoolean(BrowserLaunchActivity.IN_PROC_BROWSER);
            if (startUrl == null || endUrl == null || headerKeys == null || headerValues == null || headerKeys.length != headerValues.length) {
                return null;
            }
            return new BrowserLaunchParameters(startUrl, endUrl, headerKeys, headerValues, showUrlType, z);
        }
    }
}