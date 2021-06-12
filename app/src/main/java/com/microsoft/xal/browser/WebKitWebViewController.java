/*
 * Copyright (C) 2018-2021 Тимашков Иван
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
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.aad.adal.AuthenticationConstants;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * 05.10.2020
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
    public static final String REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS";
    public static final String REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES";

    public String endUrl = "";
    public String startUrl = "";

    public static void deleteCookies(String link, boolean z) {
        CookieManager instance = CookieManager.getInstance();
        String sslPrefix = z ? AuthenticationConstants.Broker.REDIRECT_SSL_PREFIX : "http://";
        String cookie = instance.getCookie(sslPrefix + link);
        if (cookie != null) {
            String[] split = cookie.split(AuthenticationConstants.Broker.CHALLENGE_REQUEST_CERT_AUTH_DELIMETER);
            for (String split2 : split) {
                String str2 = split2.split("=")[0];
                Log.d("WebKitWebViewController", "Deleting cookie: " + str2);
                instance.setCookie(sslPrefix, str2.trim() + "=;Domain=" + link + ";Path=/");
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            instance.flush();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        startUrl = extras.getString(START_URL, "");
        endUrl = extras.getString(END_URL, "");
        if (startUrl.isEmpty() || endUrl.isEmpty()) {
            setResult(RESULT_FAILED);
            finish();
            return;
        }

        String[] keys = extras.getStringArray(REQUEST_HEADER_KEYS);
        String[] values = extras.getStringArray(REQUEST_HEADER_VALUES);
        if (keys.length != values.length) {
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
            intent.putExtra(RESPONSE_KEY, endUrl);
            setResult(AppCompatActivity.RESULT_OK, intent);
            finish();
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == null || keys[i].isEmpty() || values[i] == null || values[i].isEmpty()) {
                setResult(RESULT_FAILED);
                finish();
                return;
            }
            hashMap.put(keys[i], values[i]);
        }

        WebView webView = new WebView(this);

        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(2);
        }
        webView.setWebChromeClient(new XalWebChromeClient());
        webView.setWebViewClient(new XalWebViewClient());
        webView.loadUrl(startUrl);
    }

    private class XalWebChromeClient extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            setProgress(progress * 100);
        }
    }

    private class XalWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, @NotNull WebResourceRequest request) {
            Intent intent = new Intent();
            intent.putExtra(WebKitWebViewController.RESPONSE_KEY, request.getUrl().toString());
            setResult(AppCompatActivity.RESULT_OK, intent);
            finish();
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, @NotNull String url) {
            if (!url.startsWith(endUrl)) {
                return false;
            }
            Intent intent = new Intent();
            intent.putExtra(WebKitWebViewController.RESPONSE_KEY, url);
            setResult(AppCompatActivity.RESULT_OK, intent);
            finish();
            return true;
        }
    }
}