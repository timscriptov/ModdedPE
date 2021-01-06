/*
 * Copyright (C) 2018-2020 Тимашков Иван
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
package com.mcal.mcpelauncher.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.microsoft.aad.adal.AuthenticationConstants;
import com.microsoft.xal.browser.ShowUrlType;

import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class WebKitWebViewControllerActivity extends Activity {
    public static final String END_URL = "END_URL";
    public static final String RESPONSE_KEY = "RESPONSE";
    public static final int RESULT_FAILED = 8054;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    private static final String TAG = "WebKitWebViewController";
    public static String startUrl;
    public static String endUrl;
    private WebView m_webView;

    public static void deleteCookies(String domain, boolean https) {
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

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (args == null) {
            Log.e(TAG, "onCreate() Called with no extras.");
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        startUrl = args.getString(START_URL, "");
        endUrl = args.getString(END_URL, "");
        if (startUrl.isEmpty() || endUrl.isEmpty()) {
            Log.e(TAG, "onCreate() Received invalid start or end URL.");
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        ShowUrlType showType = (ShowUrlType) args.get(SHOW_TYPE);
        if (showType == ShowUrlType.CookieRemoval || showType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
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

        m_webView = new WebView(this);
        m_webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            m_webView.getSettings().setMixedContentMode(2);
        }
        m_webView.setWebChromeClient(new CustomWebChromeClient());
        m_webView.setWebViewClient(new CustomWebViewClient());
        m_webView.loadUrl(startUrl);
        setContentView(m_webView);
    }

    public class CustomWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView wv, int progress) {
            setProgress(progress * 100);
        }
    }

    public class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView wv, @NotNull String url) {
            if (!url.startsWith(endUrl, 0)) {
                return false;
            }
            Log.e(TAG, "WebKitWebViewController found end URL. Ending UI flow.");
            Intent data = new Intent();
            data.putExtra(WebKitWebViewControllerActivity.RESPONSE_KEY, url);
            setResult(-1, data);
            finish();
            return true;
        }

    }
}