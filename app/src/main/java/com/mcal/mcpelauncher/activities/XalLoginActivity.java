package com.mcal.mcpelauncher.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.mcal.mcpelauncher.data.Constants;
import com.microsoft.aad.adal.AuthenticationConstants;

import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XalLoginActivity extends AppCompatActivity {
    public static final String END_URL = "END_URL";
    public static final String RESPONSE_KEY = "RESPONSE";
    public static final int RESULT_FAILED = 8054;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    private static final String TAG = "WebKitWebViewController";
    private static String startUrl;
    private static String endUrl;
    private WebView m_webView;

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (args == null) {
            Log.e(TAG, "onCreate() Called with no extras.");
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        startUrl = args.getString(START_URL, Constants.FLAVOR);
        endUrl = args.getString(END_URL, Constants.FLAVOR);
        if (startUrl.isEmpty() || endUrl.isEmpty()) {
            Log.e(TAG, "onCreate() Received invalid start or end URL.");
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        com.microsoft.xal.browser.WebView.ShowUrlType showType = (com.microsoft.xal.browser.WebView.ShowUrlType) args.get(SHOW_TYPE);
        if (showType == com.microsoft.xal.browser.WebView.ShowUrlType.CookieRemoval || showType == com.microsoft.xal.browser.WebView.ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            Log.e(TAG, "onCreate() WebView invoked for cookie removal. Deleting cookies and finishing.");
            deleteCookies("login.live.com", true);
            deleteCookies("account.live.com", true);
            deleteCookies("live.com", true);
            deleteCookies("xboxlive.com", true);
            deleteCookies("sisu.xboxlive.com", true);
            Intent data = new Intent();
            data.putExtra(RESPONSE_KEY, endUrl);
            setResult(-1, data);
            finish();
            return;
        }

        m_webView = new android.webkit.WebView(this);
        m_webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            m_webView.getSettings().setMixedContentMode(2);
        }
        m_webView.setWebChromeClient(new CustomWebChromeClient());
        m_webView.setWebViewClient(new CustomWebViewClient());
        m_webView.loadUrl(startUrl);
        setContentView(m_webView);
    }

    private void deleteCookies(String domain, boolean https) {
        CookieManager cookieManager = CookieManager.getInstance();
        String fullDomain = (https ? AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX : "http://") + domain;
        String cookieBlob = cookieManager.getCookie(fullDomain);
        boolean deletedCokies = false;
        if (cookieBlob != null) {
            String[] cookies = cookieBlob.split(AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER);
            int length = cookies.length;
            for (int i = 0; i < length; i++) {
                cookieManager.setCookie(fullDomain, cookies[i].split("=")[0].trim() + "=;Domain=" + domain + ";Path=/");
            }
            if (cookies.length > 0) {
                deletedCokies = true;
            }
        }
        if (deletedCokies) {
            Log.e(TAG, "deleteCookies() Deleted cookies for " + domain);
        } else {
            Log.e(TAG, "deleteCookies() Found no cookies for " + domain);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.flush();
        }
    }

    public class CustomWebChromeClient extends WebChromeClient {
        public void onProgressChanged(android.webkit.WebView wv, int progress) {
            setProgress(progress * 100);
        }
    }

    public class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(android.webkit.WebView wv, @NotNull String url) {
            if (!url.startsWith(endUrl, 0)) {
                return false;
            }
            Log.e(TAG, "WebKitWebViewController found end URL. Ending UI flow.");
            Intent data = new Intent();
            data.putExtra(XalLoginActivity.RESPONSE_KEY, url);
            setResult(-1, data);
            finish();
            return true;
        }
    }
}
