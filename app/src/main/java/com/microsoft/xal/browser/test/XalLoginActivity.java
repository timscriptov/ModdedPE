package com.microsoft.xal.browser.test;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XalLoginActivity extends AppCompatActivity {
    public long operationId;
    public String endurl;
    public String startUrl;
    public boolean m_cancelOperationOnResume;

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
        startUrl = getIntent().getStringExtra("start_url");
        endurl = getIntent().getStringExtra("end_url");

        android.webkit.WebView webView = new android.webkit.WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.setWebViewClient(new CustomWebViewClient());
        webView.loadUrl(startUrl);
        setContentView(webView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (!m_cancelOperationOnResume) {
            WebView.urlOperationCanceled(operationId, false, WebView.DEFAULT_BROWSER_INFO);
        }
    }

    public class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(android.webkit.WebView webView, @NotNull String finalUrl) {
            if (!finalUrl.startsWith(endurl)) {
                return false;
            }

            Log.d("XalLoginActivity", "Reached endUrl: " + finalUrl);
            WebView.urlOperationSucceeded(operationId, finalUrl, false, WebView.DEFAULT_BROWSER_INFO);
            XalLoginActivity xalLoginActivity = XalLoginActivity.this;
            xalLoginActivity.m_cancelOperationOnResume = true;
            xalLoginActivity.finish();
            return true;
        }
    }
}