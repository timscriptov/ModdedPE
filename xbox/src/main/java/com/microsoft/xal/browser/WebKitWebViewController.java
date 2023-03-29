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
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.microsoft.aad.adal.AuthenticationConstants;

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
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        String string = extras.getString(START_URL, "");
        final String string2 = extras.getString(END_URL, "");
        if (string.isEmpty() || string2.isEmpty()) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        String[] stringArray = extras.getStringArray(REQUEST_HEADER_KEYS);
        String[] stringArray2 = extras.getStringArray(REQUEST_HEADER_VALUES);
        if (stringArray.length != stringArray2.length) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        ShowUrlType showUrlType = (ShowUrlType) extras.get(SHOW_TYPE);
        if (showUrlType == ShowUrlType.CookieRemoval || showUrlType == ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            deleteCookies("login.live.com", true);
            deleteCookies("account.live.com", true);
            deleteCookies("live.com", true);
            deleteCookies("xboxlive.com", true);
            deleteCookies("sisu.xboxlive.com", true);
            Intent intent = new Intent();
            intent.putExtra(RESPONSE_KEY, string2);
            setResult(-1, intent);
            finish();
            return;
        }
        Map<String, String> hashMap = new HashMap<>(stringArray.length);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i] == null || stringArray[i].isEmpty() || stringArray2[i] == null || stringArray2[i].isEmpty()) {
                setResult(RESULT_FAILED);
                finish();
                return;
            }
            hashMap.put(stringArray[i], stringArray2[i]);
        }
        WebView webView = new WebView(this);
        this.m_webView = webView;
        setContentView(webView);
        this.m_webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            this.m_webView.getSettings().setMixedContentMode(2);
        }
        this.m_webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView2, int i2) {
                WebKitWebViewController.this.setProgress(i2 * 100);
            }
        });
        this.m_webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView2, String str) {
                super.onPageFinished(webView2, str);
                webView2.requestFocus(130);
                webView2.sendAccessibilityEvent(8);
                webView2.evaluateJavascript("if (typeof window.__xal__performAccessibilityFocus === \"function\") { window.__xal__performAccessibilityFocus(); }", null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView2, String str) {
                if (str.startsWith(string2, 0)) {
                    Intent intent2 = new Intent();
                    intent2.putExtra(WebKitWebViewController.RESPONSE_KEY, str);
                    setResult(-1, intent2);
                    finish();
                    return true;
                }
                return false;
            }
        });
        this.m_webView.loadUrl(string, hashMap);
    }

    private void deleteCookies(String str, boolean z) {
        CookieManager cookieManager = CookieManager.getInstance();
        String sb2 = (z ? AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX : "http://") + str;
        String cookie = cookieManager.getCookie(sb2);
        if (cookie != null) {
            String[] split = cookie.split(AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER);
            for (String str2 : split) {
                String trim = str2.split("=")[0].trim();
                String str3 = trim + "=;";
                if (trim.startsWith("__Secure-")) {
                    str3 = str3 + "Secure;Domain=" + str + ";Path=/";
                }
                cookieManager.setCookie(sb2, trim.startsWith("__Host-") ? str3 + "Secure;Path=/" : str3 + "Domain=" + str + ";Path=/");
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.flush();
        }
    }
}