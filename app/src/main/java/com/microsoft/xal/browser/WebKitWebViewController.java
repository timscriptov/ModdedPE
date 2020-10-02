package com.microsoft.xal.browser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.aad.adal.BuildConfig;
import com.microsoft.xal.logging.XalLogger;

/**
 * 02.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class WebKitWebViewController extends AppCompatActivity {
    public static final String END_URL = "END_URL";
    public static final String RESPONSE_KEY = "RESPONSE";
    public static final int RESULT_FAILED = 8054;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    public final XalLogger m_logger = new XalLogger("WebKitWebViewController");
    private WebView m_webView;

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (args == null) {
            m_logger.Error("onCreate() Called with no extras.");
            m_logger.Flush();
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        String startUrl = args.getString(START_URL, BuildConfig.FLAVOR);
        final String endUrl = args.getString(END_URL, BuildConfig.FLAVOR);
        if (startUrl.isEmpty() || endUrl.isEmpty()) {
            m_logger.Error("onCreate() Received invalid start or end URL.");
            m_logger.Flush();
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        com.microsoft.xal.browser.WebView.ShowUrlType showType = (com.microsoft.xal.browser.WebView.ShowUrlType) args.get(SHOW_TYPE);
        if (showType == com.microsoft.xal.browser.WebView.ShowUrlType.CookieRemoval || showType == com.microsoft.xal.browser.WebView.ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            m_logger.Important("onCreate() WebView invoked for cookie removal. Deleting cookies and finishing.");
            deleteCookies("login.live.com", true);
            deleteCookies("account.live.com", true);
            deleteCookies("live.com", true);
            deleteCookies("xboxlive.com", true);
            deleteCookies("sisu.xboxlive.com", true);
            m_logger.Flush();
            Intent data = new Intent();
            data.putExtra(RESPONSE_KEY, endUrl);
            setResult(-1, data);
            finish();
            return;
        }
        m_webView = new android.webkit.WebView(this);
        setContentView(m_webView);
        m_webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            m_webView.getSettings().setMixedContentMode(2);
        }
        m_webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(android.webkit.WebView wv, int progress) {
                setProgress(progress * 100);
            }
        });
        m_webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(android.webkit.WebView wv, String url) {
                if (!url.startsWith(endUrl, 0)) {
                    return false;
                }
                m_logger.Important("WebKitWebViewController found end URL. Ending UI flow.");
                m_logger.Flush();
                Intent data = new Intent();
                data.putExtra(WebKitWebViewController.RESPONSE_KEY, url);
                setResult(-1, data);
                finish();
                return true;
            }
        });
        m_webView.loadUrl(startUrl);
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
            m_logger.Information("deleteCookies() Deleted cookies for " + domain);
        } else {
            m_logger.Information("deleteCookies() Found no cookies for " + domain);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.flush();
        }
    }
}
