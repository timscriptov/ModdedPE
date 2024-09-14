package com.microsoft.xal.browser

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import com.microsoft.xal.browser.WebKitWebViewController.Companion.RESPONSE_KEY
import java.io.IOException

class XalWebViewClient(
    private val mActivity: Activity,
    private val mUrl: String,
) : WebViewClient() {
    override fun onPageFinished(webView: WebView, url: String) {
        super.onPageFinished(webView, url)
        webView.requestFocus(130)
        webView.sendAccessibilityEvent(8)
        webView.evaluateJavascript(
            "if (typeof window.__xal__performAccessibilityFocus === \"function\") { window.__xal__performAccessibilityFocus(); }",
            null
        )
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
        if (!url.startsWith(mUrl, 0)) {
            return false
        }
        val intent = Intent()
        intent.putExtra(RESPONSE_KEY, url)
        mActivity.setResult(RESULT_OK, intent)
        mActivity.finish()
        return true
    }

    override fun shouldInterceptRequest(
        webView: WebView,
        webResourceRequest: WebResourceRequest
    ): WebResourceResponse? {
        val uri = webResourceRequest.url.toString()
//        Log.e("XBOX_URI", uri)
        if (uri.contains("favicon.ico") || uri.contains("AppLogos")) {
            Thread { webView.loadUrl(uri) }
        }
        if (uri.contains(".css") && uri.contains("splash")) {
            Thread { webView.loadUrl(uri) }
        }
//        if (uri.contains(".css") && (uri.contains("login") || uri.contains("signup")) && !uri.contains("bootstrap")) {
//            return webResponseFromAssets("resources/splash.min.css")
//        }
//        if (uri.contains("cred_option_forgot")) {
//            return webResponseFromAssets("resources/ic_restore.png")
//        }
//        if (uri.contains("cred_option_github")) {
//            return webResponseFromAssets("resources/ic_github.png")
//        }
//        if (uri.contains("documentation")) {
//            return webResponseFromAssets("resources/ic_info.png")
//        }
        if (uri.contains("signin_options")) {
            return webResponseFromAssets("resources/ic_manage_accounts.png")
        }
        if (uri.contains("images-eds") && uri.contains("xbox")) {
            println("User Finished Login")
        }
        return if (!uri.contains("microsoft_logo") &&
            !uri.contains("AppLogos") &&
            !uri.contains("applogos") &&
            !uri.contains("xboxlivelogo") &&
            !uri.contains("logo") &&
            !uri.contains("14_298176657f8069ea5220")
        ) {
            if (!uri.contains("AppBackgrounds") && !uri.contains("appbackgrounds") && !uri.contains("73_b46031e02b69c55b4305")) {
                if (uri.contains("minecraft") && (uri.contains(".png") || uri.contains(".jpg"))) {
                    webResponseFromAssets("resources/bg32.png")
                } else super.shouldInterceptRequest(webView, webResourceRequest)
            } else webResponseFromAssets("resources/bg32.png")
        } else webResponseFromAssets("resources/title.png")
    }

    private fun webResponseFromAssets(resName: String): WebResourceResponse? {
        return try {
            WebResourceResponse("text/css", "UTF-8", mActivity.assets.open(resName))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
