package com.microsoft.onlineid;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.microsoft.onlineid.internal.AppProperties;
import com.microsoft.onlineid.internal.Uris;
import java.util.logging.Logger;

public class ResourcePreloader {
    private static final String INT_PRELOAD_URI = "https://signup.live-int.com/SignupPreload";
    private static final String PROD_PRELOAD_URI = "https://signup.live.com/SignupPreload";
    private static final Logger logger = Logger.getLogger("ResourcePreloader");

    private ResourcePreloader() {
    }

    public static void preloadSignup(Context context, String cobrandId) {
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(buildUri(context, cobrandId).toString());
    }

    private static void addWebViewClient(WebView webView) {
        webView.setWebViewClient(new WebViewClient() {
            private long started;

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                this.started = SystemClock.elapsedRealtime();
            }

            public void onLoadResource(WebView view, String url) {
                ResourcePreloader.logger.info("Loading " + url);
            }

            public void onPageFinished(WebView view, String url) {
                ResourcePreloader.logger.info("Page load for " + url + " finished in " + (SystemClock.elapsedRealtime() - this.started) + "ms");
            }
        });
    }

    private static Uri buildUri(Context context, String cobrandId) {
        return Uris.appendMarketQueryString(context.getApplicationContext(), Uri.parse(PROD_PRELOAD_URI).buildUpon().appendQueryParameter(AppProperties.CobrandIdKey, cobrandId).build());
    }
}
