package com.microsoft.xal.browser;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

public class XalLoginActivity extends AppCompatActivity {
    public WebView m_webView;
    public long operationId;
    public String endurl;
    public boolean z;

    public static void deleteCookies() {
        for (String str : new String[]{"login.live.com", "account.live.com", "live.com", "xboxlive.com", "sisu.xboxlive.com"}) {
            CookieManager instance = CookieManager.getInstance();
            String cookie = instance.getCookie("https://" + str);
            if (cookie != null) {
                for (String str2 : cookie.split(";")) {
                    int indexOf = str2.indexOf("=");
                    if (indexOf != -1) {
                        String trim = str2.substring(0, indexOf).trim();
                        Log.d("XalLoginActivity", "Deleting cookie: " + str2);
                        instance.setCookie("https://" + str, trim + "=;Expires=Thu, 01 Jan 1970 00:00:01 GMT;Domain=" + str + ";Path=/");
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().flush();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        operationId = getIntent().getLongExtra("native_op", 0);
        String stringExtra = getIntent().getStringExtra("start_url");
        endurl = getIntent().getStringExtra("end_url");
        m_webView = new WebView(this);
        m_webView.getSettings().setJavaScriptEnabled(true);
        m_webView.setWebViewClient(new CustomWebViewClient());
        Log.d("XalLoginActivity", "Sign in url is: " + stringExtra);
        Log.d("XalLoginActivity", "End url is: " + endurl);
        m_webView.loadUrl(stringExtra);
        setContentView((View) m_webView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (!z) {
            com.microsoft.xal.browser.WebView.urlOperationCanceled(operationId, false, com.microsoft.xal.browser.WebView.DEFAULT_BROWSER_INFO);
        }
    }

    public class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView webView, @NotNull String finalUrl) {
            if (!finalUrl.startsWith(endurl)) {
                return false;
            }
            Log.d("XalLoginActivity", "Reached endUrl: " + finalUrl);
            com.microsoft.xal.browser.WebView.urlOperationSucceeded(operationId, finalUrl, false, com.microsoft.xal.browser.WebView.DEFAULT_BROWSER_INFO);
            XalLoginActivity xalLoginActivity = XalLoginActivity.this;
            xalLoginActivity.z = true;
            xalLoginActivity.finish();
            return true;
        }
    }
}