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

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class WebKitWebViewController2 extends Activity {
    public static final String END_URL = "END_URL";
    public static final String RESPONSE_KEY = "RESPONSE";
    public static final int RESULT_FAILED = 8054;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    private WebView m_webView;

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        String string = extras.getString("START_URL", "");
        final String string2 = extras.getString("END_URL", "");
        if (string.isEmpty() || string2.isEmpty()) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        WebView2.ShowUrlType showUrlType = (WebView2.ShowUrlType) extras.get("SHOW_TYPE");
        if (showUrlType == WebView2.ShowUrlType.CookieRemoval || showUrlType == WebView2.ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
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
        android.webkit.WebView webView = new android.webkit.WebView(this);
        this.m_webView = webView;
        setContentView(webView);
        this.m_webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            this.m_webView.getSettings().setMixedContentMode(2);
        }
        this.m_webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(android.webkit.WebView webView, int i) {
                setProgress(i * 100);
            }
        });
        this.m_webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(android.webkit.WebView webView, String str) {
                if (!str.startsWith(string2, 0)) {
                    return false;
                }
                Intent intent = new Intent();
                intent.putExtra(WebKitWebViewController2.RESPONSE_KEY, str);
                setResult(-1, intent);
                finish();
                return true;
            }
        });
        this.m_webView.loadUrl(string);
    }

    private void deleteCookies(String str, boolean z) {
        CookieManager instance = CookieManager.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(z ? AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX : "http://");
        sb.append(str);
        String sb2 = sb.toString();
        String cookie = instance.getCookie(sb2);
        boolean z2 = false;
        if (cookie != null) {
            String[] split = cookie.split(AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER);
            for (String split2 : split) {
                String str2 = split2.split("=")[0];
                instance.setCookie(sb2, str2.trim() + "=;Domain=" + str + ";Path=/");
            }
            if (split.length > 0) {
                z2 = true;
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            instance.flush();
        }
    }
}