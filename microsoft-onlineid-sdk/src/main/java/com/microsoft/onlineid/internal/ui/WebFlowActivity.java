package com.microsoft.onlineid.internal.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.WebViewTransport;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.analytics.ITimedAnalyticsEvent;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.NetworkConnectivity;
import com.microsoft.onlineid.internal.Uris;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.log.SendLogsHandler;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.sms.SmsReceiver;
import com.microsoft.onlineid.userdata.TelephonyManagerReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WebFlowActivity extends Activity {
    public static final String ActionResolveInterrupt = "com.microsoft.onlineid.internal.RESOLVE_INTERRUPT";
    public static final String ActionSignIn = "com.microsoft.onlineid.internal.SIGN_IN";
    public static final String ActionSignUp = "com.microsoft.onlineid.internal.SIGN_UP";
    public static final String AppPropertiesHeaderName = "ms-identity-app-properties";
    private static final String JavaScriptOnBack = "javascript:OnBack()";
    private static final String ScenarioAuthUrl = "auth url";
    private static final String ScenarioSignIn = "sign in";
    private static final String ScenarioSignUp = "sign up";
    private JavaScriptBridge _javaScriptBridge;
    private SendLogsHandler _logHandler;
    private ITimedAnalyticsEvent _pageLoadTimingEvent;
    protected ProgressView _progressView;
    private String _scenario;
    private SmsReceiver _smsReceiver;
    private String _startUrl;
    private WebTelemetryRecorder _webTelemetryRecorder;
    private WebView _webView;

    private class HostedWebChromeClient extends WebChromeClient {
        private HostedWebChromeClient() {
        }

        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            try {
                ((WebViewTransport) resultMsg.obj).setWebView(new WebView(WebFlowActivity.this));
                resultMsg.sendToTarget();
                WebFlowActivity.this._javaScriptBridge.setIsOutOfBandInterrupt();
                return true;
            } catch (ClassCastException e) {
                Assertion.check(false, "resultMsg is not a WebViewTransport");
                return false;
            }
        }
    }

    private class HostedWebViewClient extends WebViewClient {
        private long _finished;
        private final BundledAssetVendor _precachedAssetVendor;
        private long _started;

        public HostedWebViewClient() {
            this._precachedAssetVendor = BundledAssetVendor.getInstance(WebFlowActivity.this.getApplicationContext());
        }

        public BundledAssetVendor getAssetVendor() {
            return this._precachedAssetVendor;
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (WebFlowActivity.this.overrideUrlLoading(view, url)) {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            WebFlowActivity.this.showLoadingStarted(view, url, favicon);
            this._started = SystemClock.elapsedRealtime();
            Logger.info("New page loaded: " + url);
        }

        public void onPageFinished(WebView view, String url) {
            this._finished = SystemClock.elapsedRealtime();
            super.onPageFinished(view, url);
            WebFlowActivity.this.showLoadingFinished(view, url);
            if (Settings.isDebugBuild()) {
                Logger.info("Page load time = " + Long.toString(this._finished - this._started));
            }
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            WebFlowActivity.this.onReceivedWebError(view, errorCode, description, failingUrl);
        }

        @TargetApi(21)
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest request) {
            return shouldInterceptRequest(webView, request.getUrl().toString());
        }

        public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
            return this._precachedAssetVendor.getAsset(url);
        }
    }

    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createInitialUI());
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().removeAllCookie();
        configureWebView(savedInstanceState, new WebFlowTelemetryData(getIntent().getExtras()));
        disableSavePasswordInWebView();
        initializeSendLogsHandler();
        Uri uri = Uris.appendMarketQueryString(getApplicationContext(), getIntent().getData());
        if (!NetworkConnectivity.isAirplaneModeOn(getApplicationContext())) {
            uri = Uris.appendPhoneDigits(new TelephonyManagerReader(getApplicationContext()), uri);
        }
        this._startUrl = uri.toString();
        if (Settings.isDebugBuild()) {
            Logger.info("Web flow starting URL: " + this._startUrl);
        }
        Map<String, String> customHeaders = new HashMap();
        String action = getIntent().getAction();
        if (ActionSignIn.equals(action) || ActionSignUp.equals(action)) {
            String appPropertiesHeader = Uris.mapToSortedQueryString(BundleMarshaller.appPropertiesFromBundle(getIntent().getExtras()).getServerValues());
            if (!TextUtils.isEmpty(appPropertiesHeader)) {
                customHeaders.put(AppPropertiesHeaderName, appPropertiesHeader);
            }
        }
        this._webView.loadUrl(this._startUrl, customHeaders);
    }

    protected void onResume() {
        super.onResume();
        this._smsReceiver = new SmsReceiver(this._javaScriptBridge);
        IntentFilter intentFilter = new IntentFilter(SmsReceiver.SMS_RECEIVED_ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(this._smsReceiver, intentFilter);
    }

    public final void onStart() {
        super.onStart();
        String action = getIntent().getAction();
        if (ActionSignIn.equals(action)) {
            this._scenario = ScenarioSignIn;
        } else if (ActionSignUp.equals(action)) {
            this._scenario = ScenarioSignUp;
        } else if (ActionResolveInterrupt.equals(action)) {
            this._scenario = ScenarioAuthUrl;
        } else if (TextUtils.isEmpty(action)) {
            this._scenario = "not specified";
        } else {
            this._scenario = action;
        }
        ClientAnalytics.get().logScreenView("Web wizard (" + this._scenario + ")");
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(this._smsReceiver);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this._webTelemetryRecorder.saveInstanceState(outState);
    }

    private RelativeLayout createInitialUI() {
        String webViewIdName;
        RelativeLayout mainView = new RelativeLayout(this);
        mainView.setLayoutParams(new LayoutParams(-1, -2));
        mainView.setBackgroundColor(-1);
        this._webView = new WebView(this);
        String action = getIntent().getAction();
        if (ActionSignIn.equals(action)) {
            webViewIdName = "msa_sdk_webflow_webview_sign_in";
        } else if (ActionSignUp.equals(action)) {
            webViewIdName = "msa_sdk_webflow_webview_sign_up";
        } else {
            webViewIdName = "msa_sdk_webflow_webview_resolve_interrupt";
        }
        this._webView.setId(getApplicationContext().getResources().getIdentifier(webViewIdName, "id", getApplicationContext().getPackageName()));
        LayoutParams webViewLayoutParams = new LayoutParams(-2, -1);
        webViewLayoutParams.addRule(10);
        mainView.addView(this._webView, webViewLayoutParams);
        this._progressView = new ProgressView(this);
        LayoutParams progressWebViewLayoutParams = new LayoutParams(-1, -2);
        progressWebViewLayoutParams.addRule(10);
        mainView.addView(this._progressView, progressWebViewLayoutParams);
        return mainView;
    }

    public static ApiRequest getFlowRequest(Context applicationContext, Uri startUri, String action, AppProperties appProperties, WebFlowTelemetryData telemetryData) {
        return new ApiRequest(applicationContext, new Intent().setClass(applicationContext, WebFlowActivity.class).setAction(action).setData(startUri).putExtra(BundleMarshaller.AppPropertiesKey, appProperties != null ? appProperties.toBundle() : null).putExtras(telemetryData.asBundle())) {
            public void executeAsync() {
                getContext().startActivity(asIntent());
            }
        };
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void configureWebView(Bundle savedInstanceState, WebFlowTelemetryData telemetryData) {
        this._webTelemetryRecorder = new WebTelemetryRecorder(telemetryData.getIsWebTelemetryRequested(), savedInstanceState);
        this._javaScriptBridge = new JavaScriptBridge(this, this._webTelemetryRecorder, telemetryData);
        this._webView.addJavascriptInterface(this._javaScriptBridge, "external");
        WebSettings webSettings = this._webView.getSettings();
        webSettings.setUserAgentString(Transport.mergeUserAgentStrings(webSettings.getUserAgentString(), Transport.buildUserAgentString(getApplicationContext())));
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        HostedWebViewClient hostedWebViewClient = new HostedWebViewClient();
        this._javaScriptBridge.setAssetBundlePropertyProvider(hostedWebViewClient.getAssetVendor());
        this._webView.setWebViewClient(hostedWebViewClient);
        this._webView.setWebChromeClient(new HostedWebChromeClient());
    }

    private void disableSavePasswordInWebView() {
        if (VERSION.SDK_INT < 18) {
            this._webView.getSettings().setSavePassword(false);
        }
    }

    private boolean overrideUrlLoading(WebView view, String url) {
        return false;
    }

    private void onReceivedWebError(WebView view, int errorCode, String description, String failingUrl) {
        view.stopLoading();
        view.loadUrl("about:blank");
        ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.NoNetworkConnectivity, ClientAnalytics.DuringWebFlow);
        sendResult(1, new ApiResult().setException(new NetworkException(String.format(Locale.US, "Error code: %d, Error description: %s, Failing url: %s", new Object[]{Integer.valueOf(errorCode), description, failingUrl}))).asBundle());
        finish();
    }

    private void showLoadingStarted(WebView view, String url, Bitmap favicon) {
        this._progressView.startAnimation();
        this._pageLoadTimingEvent = ClientAnalytics.get().createTimedEvent(ClientAnalytics.RenderingCategory, "WebWizard page load", this._scenario).start();
    }

    private void showLoadingFinished(WebView view, String url) {
        this._progressView.stopAnimation();
        if (this._pageLoadTimingEvent != null) {
            this._pageLoadTimingEvent.end();
        }
    }

    public void sendResult(int resultCode, Bundle resultData) {
        ApiRequest request = new ApiRequest(getApplicationContext(), getIntent());
        Intent continuation = request.getContinuation();
        ResultReceiver receiver = request.getResultReceiver();
        if (this._webTelemetryRecorder.hasEvents()) {
            resultData = new ApiResult(resultData).setWebFlowTelemetryFields(this._webTelemetryRecorder).asBundle();
        }
        if (continuation != null && resultCode == -1) {
            request.sendSuccess(new ApiResult(resultData));
        } else if (receiver != null) {
            receiver.send(resultCode, resultData);
        } else {
            setResult(resultCode, resultData != null ? new Intent().putExtras(resultData) : null);
        }
        finish();
        if (resultCode == -1 && (getIntent().getFlags() & 65536) == 65536) {
            overridePendingTransition(0, 0);
        }
    }

    public void cancel() {
        sendResult(0, null);
    }

    public void onBackPressed() {
        if (!this._webView.canGoBack() || this._webView.getUrl().startsWith(this._startUrl)) {
            cancel();
        } else {
            this._webView.loadUrl(JavaScriptOnBack);
        }
    }

    private void initializeSendLogsHandler() {
        if (Settings.isDebugBuild()) {
            this._logHandler = new SendLogsHandler(this);
            this._logHandler.setSendScreenshot(true);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this._logHandler != null) {
            this._logHandler.trySendLogsOnKeyEvent(keyCode);
        }
        return super.onKeyDown(keyCode, event);
    }
}
