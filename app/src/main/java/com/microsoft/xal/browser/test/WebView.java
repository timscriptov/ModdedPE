package com.microsoft.xal.browser.test;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class WebView {
    public static final String DEFAULT_BROWSER_INFO = "webkit";
    public static final int SHOW_ADDITIONAL_UI = 3;
    public static final int SHOW_DELETE_COOKIES = 1;
    public static final int SHOW_DELETE_COOKIES_ALT = 2;
    public static final int SHOW_NORMAL = 0;

    public WebView() {
    }

    public static void showUrl(long operationId, Context context, String startUrl, String endUrl, int showTypeInt, boolean useInProcBrowser, long cancelDelay) {
        Log.d("WebView", "showUrl: type=" + showTypeInt);
        if (showTypeInt == 1 || showTypeInt == 2) {
            XalLoginActivity.deleteCookies();
            urlOperationSucceeded(operationId, endUrl, false, DEFAULT_BROWSER_INFO);
            return;
        }
        if (startUrl.startsWith("https://sisu.xboxlive.com/client/v8/0000000048183522/view/splash.html?msa=")) {
            try {
                startUrl = URLDecoder.decode(startUrl.substring(74), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(context, XalLoginActivity.class);
        intent.putExtra("native_op", operationId);
        intent.putExtra("start_url", startUrl);
        intent.putExtra("end_url", endUrl);
        context.startActivity(intent);
    }

    public static native void urlOperationCanceled(long operationId, boolean sharedBrowserUsed, String browserInfo);

    public static native void urlOperationFailed(long operationId, boolean sharedBrowserUsed, String browserInfo);

    public static native void urlOperationSucceeded(long operationId, String finalUrl, boolean sharedBrowserUsed, String browserInfo);
}