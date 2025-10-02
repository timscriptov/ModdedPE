package com.microsoft.xal.browser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import com.microsoft.xal.logging.XalLogger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
public class BrowserLaunchActivity extends AppCompatActivity {
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
    private final XalLogger m_logger = new XalLogger("BrowserLaunchActivity");
    private BrowserLaunchParameters m_launchParameters = null;
    private long m_operationId = 0;
    private boolean m_customTabsInProgress = false;
    private boolean m_sharedBrowserUsed = false;
    private String m_browserInfo = null;

    private static native void checkIsLoaded();

    private static native void urlOperationCanceled(long j, boolean z, String str);

    private static native void urlOperationFailed(long j, boolean z, String str);

    private static native void urlOperationSucceeded(long j, String str, boolean z, String str2);

    public static void showUrl(long operationId, Context context, String startUrl, String endUrl, int showType, String[] requestHeaderKeys, String[] requestHeaderValues, boolean useInProcBrowser) {
        XalLogger xalLogger = new XalLogger("BrowserLaunchActivity.showUrl()");
        try {
            xalLogger.Important("JNI call received.");
            if (!startUrl.isEmpty() && !endUrl.isEmpty()) {
                ShowUrlType showUrlTypeFromInt = ShowUrlType.fromInt(showType);
                if (showUrlTypeFromInt == null) {
                    xalLogger.Error("Unrecognized show type received: " + showType);
                    urlOperationFailed(operationId, false, null);
                    xalLogger.close();
                    return;
                }
                if (requestHeaderKeys.length != requestHeaderValues.length) {
                    xalLogger.Error("requestHeaderKeys different length than requestHeaderValues.");
                    urlOperationFailed(operationId, false, null);
                    xalLogger.close();
                    return;
                }
                Intent intent = new Intent(context, BrowserLaunchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong(OPERATION_ID, operationId);
                bundle.putString(START_URL, startUrl);
                bundle.putString(END_URL, endUrl);
                bundle.putSerializable(SHOW_TYPE, showUrlTypeFromInt);
                bundle.putStringArray(REQUEST_HEADER_KEYS, requestHeaderKeys);
                bundle.putStringArray(REQUEST_HEADER_VALUES, requestHeaderValues);
                bundle.putBoolean(IN_PROC_BROWSER, useInProcBrowser);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                xalLogger.close();
                return;
            }
            xalLogger.Error("Received invalid start or end URL.");
            urlOperationFailed(operationId, false, null);
            xalLogger.close();
        } catch (Throwable th) {
            try {
                xalLogger.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.m_logger.Important("onCreate()");
        Bundle extras = getIntent().getExtras();
        if (!checkNativeCodeLoaded()) {
            this.m_logger.Warning("onCreate() Called while XAL not loaded. Dropping flow and starting app's main activity.");
            this.m_logger.Flush();
            startActivity(getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName()));
            finish();
            return;
        }
        if (bundle != null) {
            this.m_logger.Important("onCreate() Recreating with saved state.");
            this.m_operationId = bundle.getLong(OPERATION_ID_STATE_KEY);
            this.m_customTabsInProgress = bundle.getBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY);
            this.m_sharedBrowserUsed = bundle.getBoolean(SHARED_BROWSER_USED_STATE_KEY);
            this.m_browserInfo = bundle.getString(BROWSER_INFO_STATE_KEY);
            return;
        }
        if (extras != null) {
            this.m_logger.Important("onCreate() Created with intent args. Starting auth session.");
            this.m_operationId = extras.getLong(OPERATION_ID, 0L);
            BrowserLaunchParameters browserLaunchParametersFromArgs = BrowserLaunchParameters.FromArgs(extras);
            this.m_launchParameters = browserLaunchParametersFromArgs;
            if (browserLaunchParametersFromArgs == null || this.m_operationId == 0) {
                this.m_logger.Error("onCreate() Found invalid args, failing operation.");
                finishOperation(WebResult.FAIL, null);
                return;
            }
            return;
        }
        if (getIntent().getData() != null) {
            this.m_logger.Error("onCreate() Unexpectedly created with intent data. Finishing with failure.");
            setResult(RESULT_FAILED);
            finishOperation(WebResult.FAIL, null);
        } else {
            this.m_logger.Error("onCreate() Unexpectedly created, reason unknown. Finishing with failure.");
            setResult(RESULT_FAILED);
            finishOperation(WebResult.FAIL, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.m_logger.Important("onResume()");
        boolean z = this.m_customTabsInProgress;
        if (!z && this.m_launchParameters != null) {
            this.m_logger.Important("onResume() Resumed with launch parameters. Starting auth session.");
            BrowserLaunchParameters browserLaunchParameters = this.m_launchParameters;
            this.m_launchParameters = null;
            startAuthSession(browserLaunchParameters);
            return;
        }
        if (z) {
            this.m_customTabsInProgress = false;
            Uri data = getIntent().getData();
            if (data != null) {
                this.m_logger.Important("onResume() Resumed with intent data. Finishing operation successfully.");
                finishOperation(WebResult.SUCCESS, data.toString());
                return;
            } else {
                this.m_logger.Warning("onResume() Resumed with no intent data. Canceling operation.");
                finishOperation(WebResult.CANCEL, null);
                return;
            }
        }
        this.m_logger.Warning("onResume() No action to take. This shouldn't happen.");
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.m_logger.Important("onSaveInstanceState() Preserving state.");
        bundle.putLong(OPERATION_ID_STATE_KEY, this.m_operationId);
        bundle.putBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY, this.m_customTabsInProgress);
        bundle.putBoolean(SHARED_BROWSER_USED_STATE_KEY, this.m_sharedBrowserUsed);
        bundle.putString(BROWSER_INFO_STATE_KEY, this.m_browserInfo);
    }

    @Override
    public void onNewIntent(@NotNull Intent intent) {
        super.onNewIntent(intent);
        this.m_logger.Important("onNewIntent() Received intent.");
        setIntent(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.m_logger.Important("onActivityResult() Result received.");
        if (requestCode == WEB_KIT_WEB_VIEW_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String responseKey = data.getExtras().getString(WebKitWebViewController.RESPONSE_KEY, "");
                if (responseKey.isEmpty()) {
                    this.m_logger.Error("onActivityResult() Invalid final URL received from web view.");
                } else {
                    finishOperation(WebResult.SUCCESS, responseKey);
                    return;
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finishOperation(WebResult.CANCEL, null);
                return;
            } else if (resultCode != WebKitWebViewController.RESULT_FAILED) {
                this.m_logger.Warning("onActivityResult() Unrecognized result code received from web view:" + resultCode);
            }
            finishOperation(WebResult.FAIL, null);
            return;
        }
        this.m_logger.Warning("onActivityResult() Result received from unrecognized request.");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.m_logger.Important("onDestroy()");
        if (!isFinishing() || this.m_operationId == 0) {
            return;
        }
        this.m_logger.Warning("onDestroy() Activity is finishing with operation in progress, canceling.");
        finishOperation(WebResult.CANCEL, null);
    }

    private void startAuthSession(@NotNull BrowserLaunchParameters browserLaunchParameters) {
        try {
            BrowserSelectionResult browserSelectionResultSelectBrowser = BrowserSelector.selectBrowser(getApplicationContext(), browserLaunchParameters.UseInProcBrowser);
            this.m_browserInfo = browserSelectionResultSelectBrowser.toString();
            this.m_logger.Important("startAuthSession() Set browser info: " + this.m_browserInfo);
            this.m_logger.Important("startAuthSession() Starting auth session for ShowUrlType: " + browserLaunchParameters.ShowType.toString());
            String packageName = browserSelectionResultSelectBrowser.packageName();
            if (packageName == null) {
                this.m_logger.Important("startAuthSession() BrowserSelector returned null package name. Choosing WebKit strategy.");
                startWebView(browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType, browserLaunchParameters.RequestHeaderKeys, browserLaunchParameters.RequestHeaderValues);
            } else {
                this.m_logger.Important("startAuthSession() BrowserSelector returned non-null package name. Choosing CustomTabs strategy.");
                startCustomTabsInBrowser(packageName, browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType);
            }
        } catch (NoSuchAlgorithmException e) {
            this.m_logger.Important("startAuthSession() BrowserSelector returned null package name. Choosing WebKit strategy.");
            startWebView(browserLaunchParameters.StartUrl, browserLaunchParameters.EndUrl, browserLaunchParameters.ShowType, browserLaunchParameters.RequestHeaderKeys, browserLaunchParameters.RequestHeaderValues);
        }
    }

    private void startCustomTabsInBrowser(String packageName, String startUrl, String endUrl, ShowUrlType showUrlType) {
        if (showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, endUrl);
            return;
        }
        this.m_customTabsInProgress = true;
        this.m_sharedBrowserUsed = true;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setShowTitle(true);
        CustomTabsIntent customTabsIntentBuild = builder.build();
        customTabsIntentBuild.intent.setData(Uri.parse(startUrl));
        customTabsIntentBuild.intent.setPackage(packageName);
        startActivity(customTabsIntentBuild.intent);
    }

    private void startWebView(String startUrl, String endUrl, ShowUrlType showUrlType, String[] requestHeaderKeys, String[] requestHeaderValues) {
        this.m_sharedBrowserUsed = false;
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

    private void finishOperation(WebResult webResult, String str) {
        long operatioId = this.m_operationId;
        this.m_operationId = 0L;
        finish();
        if (operatioId == 0) {
            this.m_logger.Error("finishOperation() No operation ID to complete.");
            this.m_logger.Flush();
            return;
        }
        this.m_logger.Flush();
        switch (webResult) {
            case SUCCESS:
                urlOperationSucceeded(operatioId, str, this.m_sharedBrowserUsed, this.m_browserInfo);
                break;
            case CANCEL:
                urlOperationCanceled(operatioId, this.m_sharedBrowserUsed, this.m_browserInfo);
                break;
            case FAIL:
                urlOperationFailed(operatioId, this.m_sharedBrowserUsed, this.m_browserInfo);
                break;
        }
    }

    private boolean checkNativeCodeLoaded() {
        try {
            checkIsLoaded();
            return true;
        } catch (UnsatisfiedLinkError unused) {
            this.m_logger.Error("checkNativeCodeLoaded() Caught UnsatisfiedLinkError, native code not loaded");
            return false;
        }
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

        @Contract(pure = true)
        public static @Nullable ShowUrlType fromInt(int i) {
            if (i == 0) {
                return Normal;
            }
            if (i == 1) {
                return CookieRemoval;
            }
            if (i == 2) {
                return CookieRemovalSkipIfSharedCredentials;
            }
            if (i != 3) {
                return null;
            }
            return NonAuthFlow;
        }

        @Override
        public @NotNull String toString() {
            switch (this) {
                case Normal:
                    return "Normal";
                case CookieRemoval:
                    return "CookieRemoval";
                case CookieRemovalSkipIfSharedCredentials:
                    return "CookieRemovalSkipIfSharedCredentials";
                case NonAuthFlow:
                    return "NonAuthFlow";
                default:
                    return "Unknown";
            }
        }
    }

    private static class BrowserLaunchParameters {
        public final String EndUrl;
        public final String[] RequestHeaderKeys;
        public final String[] RequestHeaderValues;
        public final ShowUrlType ShowType;
        public final String StartUrl;
        public boolean UseInProcBrowser;

        private BrowserLaunchParameters(String startUrl, String endUrl, String[] requestHeaderKeys, String[] requestHeaderValues, ShowUrlType showUrlType, boolean z) {
            XalLogger xalLogger = new XalLogger("BrowserLaunchActivity.BrowserLaunchParameters");
            try {
                this.StartUrl = startUrl;
                this.EndUrl = endUrl;
                this.RequestHeaderKeys = requestHeaderKeys;
                this.RequestHeaderValues = requestHeaderValues;
                this.ShowType = showUrlType;
                if (showUrlType == ShowUrlType.NonAuthFlow) {
                    xalLogger.Important("BrowserLaunchParameters() Forcing inProc browser because flow is marked non-auth.");
                } else {
                    if (requestHeaderKeys.length > 0) {
                        xalLogger.Important("BrowserLaunchParameters() Forcing inProc browser because request headers were found.");
                    }
                    this.UseInProcBrowser = z;
                    xalLogger.close();
                }
                z = true;
                this.UseInProcBrowser = z;
                xalLogger.close();
            } catch (Throwable th) {
                try {
                    xalLogger.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        }

        public static @Nullable BrowserLaunchParameters FromArgs(@NotNull Bundle bundle) {
            String startUrl = bundle.getString("START_URL");
            String endUrl = bundle.getString("END_URL");
            String[] requestHeaderKeys = bundle.getStringArray("REQUEST_HEADER_KEYS");
            String[] requestHeaderValues = bundle.getStringArray("REQUEST_HEADER_VALUES");
            ShowUrlType showUrlType = (ShowUrlType) bundle.get("SHOW_TYPE");
            boolean z = bundle.getBoolean(BrowserLaunchActivity.IN_PROC_BROWSER);
            if (startUrl == null || endUrl == null || requestHeaderKeys == null || requestHeaderValues == null || requestHeaderKeys.length != requestHeaderValues.length) {
                return null;
            }
            return new BrowserLaunchParameters(startUrl, endUrl, requestHeaderKeys, requestHeaderValues, showUrlType, z);
        }
    }
}