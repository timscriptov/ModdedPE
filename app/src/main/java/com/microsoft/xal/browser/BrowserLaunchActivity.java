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
    private static final String BROWSER_INFO_STATE_KEY = "BROWSER_INFO_STATE";
    private static final String CUSTOM_TABS_IN_PROGRESS_STATE_KEY = "CUSTOM_TABS_IN_PROGRESS_STATE";
    public static final String END_URL = "END_URL";
    public static final String IN_PROC_BROWSER = "IN_PROC_BROWSER";
    public static final String OPERATION_ID = "OPERATION_ID";
    private static final String OPERATION_ID_STATE_KEY = "OPERATION_ID_STATE";
    public static final String REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS";
    public static final String REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES";
    public static final int RESULT_FAILED = 8052;
    private static final String SHARED_BROWSER_USED_STATE_KEY = "SHARED_BROWSER_USED_STATE";
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    public static final int WEB_KIT_WEB_VIEW_REQUEST = 8053;
    private BrowserLaunchParameters m_launchParameters = null;
    private long m_operationId = 0;
    private boolean m_customTabsInProgress = false;
    private boolean m_sharedBrowserUsed = false;
    private String m_browserInfo = null;

    public enum WebResult {
        SUCCESS,
        FAIL,
        CANCEL
    }

    private static native void checkIsLoaded();

    private static native void urlOperationCanceled(long j, boolean z, String str);

    private static native void urlOperationFailed(long j, boolean z, String str);

    private static native void urlOperationSucceeded(long j, String str, boolean z, String str2);

    public static class BrowserLaunchParameters {
        public final String EndUrl;
        public final String[] RequestHeaderKeys;
        public final String[] RequestHeaderValues;
        public final ShowUrlType ShowType;
        public final String StartUrl;
        public boolean UseInProcBrowser;

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

        private BrowserLaunchParameters(String str, String str2, String[] strArr, String[] strArr2, ShowUrlType showUrlType, boolean z) {
            this.StartUrl = str;
            this.EndUrl = str2;
            this.RequestHeaderKeys = strArr;
            this.RequestHeaderValues = strArr2;
            this.ShowType = showUrlType;
            this.UseInProcBrowser = true;
        }
    }

    public static void showUrl(long j, Context context, @NonNull String str, String str2, int i, String[] strArr, String[] strArr2, boolean z) {
        if (!str.isEmpty() && !str2.isEmpty()) {
            ShowUrlType fromInt = ShowUrlType.fromInt(i);
            if (fromInt == null) {
                urlOperationFailed(j, false, null);
                return;
            } else if (strArr.length != strArr2.length) {
                urlOperationFailed(j, false, null);
                return;
            } else {
                Intent intent = new Intent(context, BrowserLaunchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(OPERATION_ID, j);
                bundle.putString(START_URL, str);
                bundle.putString(END_URL, str2);
                bundle.putSerializable(SHOW_TYPE, fromInt);
                bundle.putStringArray(REQUEST_HEADER_KEYS, strArr);
                bundle.putStringArray(REQUEST_HEADER_VALUES, strArr2);
                bundle.putBoolean(IN_PROC_BROWSER, z);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        }
        urlOperationFailed(j, false, null);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (!checkNativeCodeLoaded()) {
            startActivity(getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName()));
            finish();
        } else if (bundle != null) {
            this.m_operationId = bundle.getLong(OPERATION_ID_STATE_KEY);
            this.m_customTabsInProgress = bundle.getBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY);
            this.m_sharedBrowserUsed = bundle.getBoolean(SHARED_BROWSER_USED_STATE_KEY);
            this.m_browserInfo = bundle.getString(BROWSER_INFO_STATE_KEY);
        } else if (extras != null) {
            this.m_operationId = extras.getLong(OPERATION_ID, 0L);
            BrowserLaunchParameters FromArgs = BrowserLaunchParameters.FromArgs(extras);
            this.m_launchParameters = FromArgs;
            if (FromArgs != null && this.m_operationId != 0) {
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
        boolean z = this.m_customTabsInProgress;
        if (!z && this.m_launchParameters != null) {
            BrowserLaunchParameters browserLaunchParameters = this.m_launchParameters;
            this.m_launchParameters = null;
            startAuthSession(browserLaunchParameters);
        } else if (z) {
            this.m_customTabsInProgress = false;
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
        bundle.putLong(OPERATION_ID_STATE_KEY, this.m_operationId);
        bundle.putBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY, this.m_customTabsInProgress);
        bundle.putBoolean(SHARED_BROWSER_USED_STATE_KEY, this.m_sharedBrowserUsed);
        bundle.putString(BROWSER_INFO_STATE_KEY, this.m_browserInfo);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 8053) {
            if (i2 == -1) {
                String string = intent.getExtras().getString(WebKitWebViewController.RESPONSE_KEY, "");
                if (!string.isEmpty()) {
                    finishOperation(WebResult.SUCCESS, string);
                    return;
                }
            } else if (i2 == 0) {
                finishOperation(WebResult.CANCEL, null);
                return;
            }
            finishOperation(WebResult.FAIL, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isFinishing() || this.m_operationId == 0) {
            return;
        }
        finishOperation(WebResult.CANCEL, null);
    }

    private void startAuthSession(@NonNull BrowserLaunchParameters browserLaunchParameters) {
        BrowserSelectionResult selectBrowser = BrowserSelector.selectBrowser(getApplicationContext(), browserLaunchParameters.UseInProcBrowser);
        this.m_browserInfo = selectBrowser.toString();
        String packageName = selectBrowser.packageName();
        if (packageName == null) {
            startWebView(browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType, browserLaunchParameters.RequestHeaderKeys, browserLaunchParameters.RequestHeaderValues);
            return;
        }
        startCustomTabsInBrowser(packageName, browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType);
    }

    private void startCustomTabsInBrowser(String str, String str2, String str3, ShowUrlType showUrlType) {
        if (showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, str3);
            return;
        }
        this.m_customTabsInProgress = true;
        this.m_sharedBrowserUsed = true;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        CustomTabsIntent build = builder.build();
        build.intent.setData(Uri.parse(str2));
        build.intent.setPackage(str);
        startActivity(build.intent);
    }

    private void startWebView(String str, String str2, ShowUrlType showUrlType, String[] strArr, String[] strArr2) {
        this.m_sharedBrowserUsed = false;
        Intent intent = new Intent(getApplicationContext(), WebKitWebViewController.class);
        Bundle bundle = new Bundle();
        bundle.putString(START_URL, str);
        bundle.putString(END_URL, str2);
        bundle.putSerializable(SHOW_TYPE, showUrlType);
        bundle.putStringArray(REQUEST_HEADER_KEYS, strArr);
        bundle.putStringArray(REQUEST_HEADER_VALUES, strArr2);
        intent.putExtras(bundle);
        startActivityForResult(intent, WEB_KIT_WEB_VIEW_REQUEST);
    }

    private void finishOperation(WebResult webResult, String str) {
        long j = this.m_operationId;
        this.m_operationId = 0L;
        finish();
        if (j == 0) {
            return;
        }
        int result = XalWebResult.mWebResult[webResult.ordinal()];
        if (result == 1) {
            urlOperationSucceeded(j, str, this.m_sharedBrowserUsed, this.m_browserInfo);
        } else if (result == 2) {
            urlOperationCanceled(j, this.m_sharedBrowserUsed, this.m_browserInfo);
        } else if (result != 3) {
        } else {
            urlOperationFailed(j, this.m_sharedBrowserUsed, this.m_browserInfo);
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
}