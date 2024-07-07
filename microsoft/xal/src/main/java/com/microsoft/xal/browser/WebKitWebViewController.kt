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
package com.microsoft.xal.browser

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.*
import android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
class WebKitWebViewController : AppCompatActivity() {
    private var mWebView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        val extras = intent.extras
        if (extras == null) {
            setResult(RESULT_FAILED)
            finish()
            return
        }
        val url = extras.getString(START_URL, "")
        val endUrl = extras.getString(END_URL, "")
        if (url.isEmpty() || endUrl.isEmpty()) {
            setResult(RESULT_FAILED)
            finish()
            return
        }
        val requestHeaderKeys = extras.getStringArray(REQUEST_HEADER_KEYS)
        val requestHeaderValues = extras.getStringArray(REQUEST_HEADER_VALUES)
        if (!requestHeaderKeys.isNullOrEmpty() && !requestHeaderValues.isNullOrEmpty()) {
            if (requestHeaderKeys.size != requestHeaderValues.size) {
                setResult(RESULT_FAILED)
                finish()
                return
            }
            val hashMap: MutableMap<String?, String?> = HashMap(requestHeaderKeys.size)
            for (i in requestHeaderKeys.indices) {
                if (requestHeaderKeys[i] == null || requestHeaderKeys[i]!!
                        .isEmpty() || requestHeaderValues[i] == null || requestHeaderValues[i]!!
                        .isEmpty()
                ) {
                    setResult(RESULT_FAILED)
                    finish()
                    return
                }
                hashMap[requestHeaderKeys[i]] = requestHeaderValues[i]
            }
        }
        val showUrlType = extras[SHOW_TYPE] as ShowUrlType?
        if (showUrlType === ShowUrlType.CookieRemoval || showUrlType === ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            deleteCookies("login.live.com", true)
            deleteCookies("account.live.com", true)
            deleteCookies("live.com", true)
            deleteCookies("xboxlive.com", true)
            deleteCookies("sisu.xboxlive.com", true)
            setResult(RESULT_OK, Intent().apply {
                putExtra(RESPONSE_KEY, endUrl)
            })
            finish()
            return
        }
        val webView = WebView(this)
        setContentView(webView)
        webView.apply {
            settings.javaScriptEnabled = true
            settings.mixedContentMode = MIXED_CONTENT_COMPATIBILITY_MODE
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(webView: WebView, i: Int) {
                    setProgress(i * 100)
                }
            }
            webViewClient = XalWebViewClient(this@WebKitWebViewController, endUrl)
            loadUrl(url)
        }.also { mWebView = it }
    }

    private fun deleteCookies(domain: String, useHttps: Boolean) {
        val cookieManager = CookieManager.getInstance()
        val url = (if (useHttps) {
            "https://"
        } else {
            "http://"
        }) + domain
        cookieManager.getCookie(url)?.let { cookie ->
            val isDeleted = false
            val split = cookie.split(";".toRegex()).dropLastWhile {
                it.isEmpty()
            }.toTypedArray()
            for (str2 in split) {
                val trim = str2.split("=".toRegex()).dropLastWhile {
                    it.isEmpty()
                }.toTypedArray()[0].trim()
                var str3 = "$trim=;"
                if (trim.startsWith("__Secure-")) {
                    str3 = str3 + "Secure;Domain=" + domain + ";Path=/"
                }
                cookieManager.setCookie(
                    url,
                    if (trim.startsWith("__Host-")) {
                        str3 + "Secure;Path=/"
                    } else {
                        str3 + "Domain=" + domain + ";Path=/"
                    }
                )
            }
            if (isDeleted) {
                println("deleteCookies() Deleted cookies for $domain");
            } else {
                println("deleteCookies() Found no cookies for $domain");
            }
        }
        cookieManager.flush()
    }

    inner class XalWebViewClient(
        private val mActivity: Activity,
        private val mUrl: String,
    ) : WebViewClient() {
        override fun onPageFinished(webView: WebView, url: String) {
            super.onPageFinished(webView, url)
            webView.apply {
                requestFocus(130)
                sendAccessibilityEvent(8)
                evaluateJavascript(
                    "if (typeof window.__xal__performAccessibilityFocus === \"function\") { window.__xal__performAccessibilityFocus(); }",
                    null
                )
            }
        }

        @Deprecated("Deprecated in Java")
        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            if (url.startsWith(mUrl, 0)) {
                setResult(RESULT_OK, Intent().apply {
                    putExtra(RESPONSE_KEY, url)
                })
                finish()
                return true
            }
            return false
        }

        override fun shouldInterceptRequest(
            webView: WebView,
            webResourceRequest: WebResourceRequest
        ): WebResourceResponse? {
            val uri = webResourceRequest.url.toString()
            if (uri.contains("favicon.ico") || uri.contains("AppLogos")) {
                Thread { webView.loadUrl(uri) }
            }
            if (uri.contains(".css") && uri.contains("splash")) {
                Thread { webView.loadUrl(uri) }
            }
            if (uri.contains(".css") && (uri.contains("login") || uri.contains("signup")) && !uri.contains(
                    "bootstrap"
                )
            ) {
                return webResponseFromAssets("resources/login.css")
            }
            if (uri.contains("images-eds") && uri.contains("xbox")) {
                println("User Finished Login")
            }
            return if (!uri.contains("microsoft_logo") &&
                !uri.contains("AppLogos") &&
                !uri.contains("applogos") &&
                !uri.contains("xboxlivelogo") &&
                !uri.contains("logo")
            ) {
                if (!uri.contains("AppBackgrounds") && !uri.contains("appbackgrounds")) {
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

    companion object {
        const val END_URL = "END_URL"
        const val REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS"
        const val REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES"
        const val RESPONSE_KEY = "RESPONSE"
        const val RESULT_FAILED = 8052
        const val SHOW_TYPE = "SHOW_TYPE"
        const val START_URL = "START_URL"
    }
}
