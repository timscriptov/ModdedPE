package com.mcal.mcpelauncher.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XalLoginActivity extends AppCompatActivity {
    private static final String TAG = "XalLoginActivity";
    public static final String RESPONSE_KEY = "RESPONSE";
    private WebView m_webView;

    public long nativeOp;
    public String startUrl;
    public String endUrl;
    public boolean r0;

    @SuppressLint("SetJavaScriptEnabled")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nativeOp = getIntent().getLongExtra("native_op", 0);
        startUrl = getIntent().getStringExtra("start_url");
        endUrl = getIntent().getStringExtra("end_url");

        m_webView = new android.webkit.WebView(this);
        m_webView.getSettings().setJavaScriptEnabled(true);
        m_webView.setWebViewClient(new CustomWebViewClient());
        m_webView.loadUrl(startUrl);
        setContentView(m_webView);
    }

    public static void deleteCookies() {
        for (String domain : new String[]{"login.live.com", "account.live.com", "live.com", "xboxlive.com", "sisu.xboxlive.com"}) {
            CookieManager instance = CookieManager.getInstance();
            String cookie = instance.getCookie("https://" + domain);
            if (cookie != null) {
                for (String cookies : cookie.split(";")) {
                    int indexOf = cookies.indexOf("=");
                    if (indexOf != -1) {
                        String trim = cookies.substring(0, indexOf).trim();
                        Log.d("XalLoginActivity", "Deleting cookie: " + cookies);
                        instance.setCookie("https://" + domain, trim + "=;Expires=Thu, 01 Jan 1970 00:00:01 GMT;Domain=" + domain + ";Path=/");
                    }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().flush();
        }
    }


    public class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(android.webkit.WebView wv, @NotNull String url) {
            if (!url.startsWith(endUrl)) {
                return false;
            }
            Log.d(TAG, "Reached endUrl: " + url);
            com.microsoft.xal.browser.WebView.urlOperationSucceeded(nativeOp, url, false, com.microsoft.xal.browser.WebView.DEFAULT_BROWSER_INFO);
            XalLoginActivity xalLoginActivity = XalLoginActivity.this;
            xalLoginActivity.r0 = true;
            xalLoginActivity.finish();
            return true;
        }
    }
}
