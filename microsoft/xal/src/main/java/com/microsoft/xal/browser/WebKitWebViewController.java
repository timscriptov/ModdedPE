package com.microsoft.xal.browser;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import com.microsoft.xal.logging.XalLogger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
 */
public class WebKitWebViewController extends AppCompatActivity {
    public static final String END_URL = "END_URL";
    public static final String REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS";
    public static final String REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES";
    public static final String RESPONSE_KEY = "RESPONSE";
    public static final int RESULT_FAILED = 8054;
    public static final String SHOW_TYPE = "SHOW_TYPE";
    public static final String START_URL = "START_URL";
    private final XalLogger m_logger = new XalLogger("WebKitWebViewController");
    private WebView m_webView;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            this.m_logger.Error("onCreate() Called with no extras.");
            this.m_logger.Flush();
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        String startUrl = extras.getString(START_URL, "");
        final String endUrl = extras.getString(END_URL, "");
        if (startUrl.isEmpty() || endUrl.isEmpty()) {
            this.m_logger.Error("onCreate() Received invalid start or end URL.");
            this.m_logger.Flush();
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        String[] requestHeaderKeys = extras.getStringArray(REQUEST_HEADER_KEYS);
        String[] requestHeaderValues = extras.getStringArray(REQUEST_HEADER_VALUES);
        if (requestHeaderKeys.length != requestHeaderValues.length) {
            this.m_logger.Error("onCreate() Received request header and key arrays of different lengths.");
            this.m_logger.Flush();
            setResult(RESULT_FAILED);
            finish();
            return;
        }
        BrowserLaunchActivity.ShowUrlType showUrlType = (BrowserLaunchActivity.ShowUrlType) extras.get(SHOW_TYPE);
        if (showUrlType == BrowserLaunchActivity.ShowUrlType.CookieRemoval || showUrlType == BrowserLaunchActivity.ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            this.m_logger.Important("onCreate() WebView invoked for cookie removal. Deleting cookies and finishing.");
            if (requestHeaderKeys.length > 0) {
                this.m_logger.Warning("onCreate() WebView invoked for cookie removal with requestHeaders.");
            }
            deleteCookies("login.live.com", true);
            deleteCookies("account.live.com", true);
            deleteCookies("live.com", true);
            deleteCookies("xboxlive.com", true);
            deleteCookies("sisu.xboxlive.com", true);
            this.m_logger.Flush();
            Intent intent = new Intent();
            intent.putExtra(RESPONSE_KEY, endUrl);
            setResult(-1, intent);
            finish();
            return;
        }
        HashMap<String, String> map = new HashMap<>(requestHeaderKeys.length);
        for (int i = 0; i < requestHeaderKeys.length; i++) {
            String key = requestHeaderKeys[i];
            String value = requestHeaderValues[i];
            if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                this.m_logger.Error("onCreate() Received null or empty request field.");
                this.m_logger.Flush();
                setResult(RESULT_FAILED);
                finish();
                return;
            }
            map.put(requestHeaderKeys[i], requestHeaderValues[i]);
        }
        WebView webView = new WebView(this);
        this.m_webView = webView;
        setContentView(webView);
        this.m_webView.getSettings().setJavaScriptEnabled(true);
        this.m_webView.getSettings().setMixedContentMode(2);
        this.m_webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView2, int i2) {
                setProgress(i2 * 100);
            }
        });
        this.m_webView.setWebViewClient(new XalWebViewClient(this, endUrl));
        this.m_webView.loadUrl(startUrl, map);
    }

    private void deleteCookies(String domain, boolean useHttps) {
        CookieManager cookieManager = CookieManager.getInstance();
        String url = (useHttps ? "https://" : "http://") + domain;
        String cookies = cookieManager.getCookie(url);

        boolean cookiesDeleted = deleteDomainCookies(cookieManager, url, cookies);

        logDeletionResult(domain, cookiesDeleted);
        cookieManager.flush();
    }

    private boolean deleteDomainCookies(CookieManager cookieManager, String url, String cookies) {
        if (cookies == null) {
            return false;
        }

        String[] cookiePairs = cookies.split(";");
        for (String cookiePair : cookiePairs) {
            String cookieName = extractCookieName(cookiePair);
            String deletionCookie = buildDeletionCookie(cookieName, url);
            cookieManager.setCookie(url, deletionCookie);
        }

        return cookiePairs.length > 0;
    }

    private @NotNull String extractCookieName(@NotNull String cookiePair) {
        return cookiePair.split("=")[0].trim();
    }

    private @NotNull String buildDeletionCookie(String cookieName, String domain) {
        StringBuilder deletionCookie = new StringBuilder();
        deletionCookie.append(cookieName).append("=;");

        if (cookieName.startsWith("__Host-")) {
            deletionCookie.append("Secure;Path=/");
        } else if (cookieName.startsWith("__Secure-")) {
            deletionCookie.append("Secure;Domain=").append(extractDomain(domain)).append(";Path=/");
        } else {
            deletionCookie.append("Domain=").append(extractDomain(domain)).append(";Path=/");
        }

        return deletionCookie.toString();
    }

    private @NotNull String extractDomain(@NotNull String url) {
        return url.replaceAll("^https?://", "");
    }

    private void logDeletionResult(String domain, boolean cookiesDeleted) {
        if (cookiesDeleted) {
            m_logger.Information("deleteCookies() Deleted cookies for " + domain);
        } else {
            m_logger.Information("deleteCookies() Found no cookies for " + domain);
        }
    }
}