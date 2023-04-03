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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.aad.adal.AuthenticationConstants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 13.08.2022
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class WebKitWebViewController extends Activity {
    public static final String END_URL = "END_URL";
    public static final String REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS";
    public static final String REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES";
    public static final String RESPONSE_KEY = "RESPONSE";
    public static final int RESULT_FAILED = 8054;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    private WebView m_webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        final Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        final String url = extras.getString(START_URL, "");
        final String endUrl = extras.getString(END_URL, "");
        if (url.isEmpty() || endUrl.isEmpty()) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        final String[] requestHeaderKeys = extras.getStringArray(REQUEST_HEADER_KEYS);
        final String[] requestHeaderValues = extras.getStringArray(REQUEST_HEADER_VALUES);
        if (requestHeaderKeys.length != requestHeaderValues.length) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        final ShowUrlType showUrlType = (ShowUrlType) extras.get(SHOW_TYPE);
        if (showUrlType == ShowUrlType.CookieRemoval || showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            deleteCookies("login.live.com", true);
            deleteCookies("account.live.com", true);
            deleteCookies("live.com", true);
            deleteCookies("xboxlive.com", true);
            deleteCookies("sisu.xboxlive.com", true);
            Intent intent = new Intent();
            intent.putExtra(RESPONSE_KEY, endUrl);
            setResult(-1, intent);
            finish();
            return;
        }
        final Map<String, String> hashMap = new HashMap<>(requestHeaderKeys.length);
        for (int i = 0; i < requestHeaderKeys.length; i++) {
            if (requestHeaderKeys[i] == null || requestHeaderKeys[i].isEmpty() || requestHeaderValues[i] == null || requestHeaderValues[i].isEmpty()) {
                setResult(RESULT_FAILED);
                finish();
                return;
            }
            hashMap.put(requestHeaderKeys[i], requestHeaderValues[i]);
        }
        final WebView webView = new WebView(this);
        m_webView = webView;
        setContentView(webView);
        m_webView.getSettings().setJavaScriptEnabled(true);
        m_webView.getSettings().setMixedContentMode(2);
        m_webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                setProgress(i * 100);
            }
        });
        m_webView.setWebViewClient(new XalWebViewClient(this, endUrl));
        m_webView.loadUrl(url);
    }

    private void deleteCookies(String domain, boolean useHttps) {
        final CookieManager cookieManager = CookieManager.getInstance();
        final String url = (useHttps ? AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX : "http://") + domain;
        final String cookie = cookieManager.getCookie(url);
        if (cookie != null) {
            final String[] split = cookie.split(AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER);
            for (String str2 : split) {
                final String trim = str2.split("=")[0].trim();
                String str3 = trim + "=;";
                if (trim.startsWith("__Secure-")) {
                    str3 = str3 + "Secure;Domain=" + domain + ";Path=/";
                }
                cookieManager.setCookie(url, trim.startsWith("__Host-") ? str3 + "Secure;Path=/" : str3 + "Domain=" + domain + ";Path=/");
            }
        }
        cookieManager.flush();
    }

    public class XalWebViewClient extends WebViewClient {
        private final Activity mActivity;
        private final String mUrl;

        public XalWebViewClient(Activity activity, String url) {
            mActivity = activity;
            mUrl = url;
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            webView.requestFocus(130);
            webView.sendAccessibilityEvent(8);
            webView.evaluateJavascript("if (typeof window.__xal__performAccessibilityFocus === \"function\") { window.__xal__performAccessibilityFocus(); }", null);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView2, @NonNull String str) {
            if (str.startsWith(mUrl, 0)) {
                Intent intent2 = new Intent();
                intent2.putExtra(WebKitWebViewController.RESPONSE_KEY, str);
                setResult(-1, intent2);
                finish();
                return true;
            }
            return false;
        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, @NonNull WebResourceRequest webResourceRequest) {
            final String uri = webResourceRequest.getUrl().toString();
            if (uri.contains("favicon.ico") || uri.contains("AppLogos")) {
                new Thread(() -> webView.loadUrl(uri));
            }
            if (uri.contains(".css") && uri.contains("splash")) {
                new Thread(() -> webView.loadUrl(uri));
            }
            if (uri.contains(".css") && ((uri.contains("login") || uri.contains("signup")) && !uri.contains("bootstrap"))) {
                return webResponseFromAssets("resources/login.css");
            }
            if (uri.contains("images-eds") && uri.contains("xbox")) {
                System.out.println("User Finished Login");
            }
            if (!uri.contains("microsoft_logo") &&
                    !uri.contains("AppLogos") &&
                    !uri.contains("applogos") &&
                    !uri.contains("xboxlivelogo") &&
                    !uri.contains("logo")) {
                if (!uri.contains("AppBackgrounds") && !uri.contains("appbackgrounds")) {
                    if (uri.contains("minecraft") && (uri.contains(".png") || uri.contains(".jpg"))) {
                        return webResponseFromAssets("resources/bg32.png");
                    } else return super.shouldInterceptRequest(webView, webResourceRequest);
                } else return webResponseFromAssets("resources/bg32.png");
            } else return webResponseFromAssets("resources/title.png");
        }

        @Nullable
        private WebResourceResponse webResponseFromAssets(String resName) {
            try {
                return new WebResourceResponse("text/css", "UTF-8", mActivity.getAssets().open(resName));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}