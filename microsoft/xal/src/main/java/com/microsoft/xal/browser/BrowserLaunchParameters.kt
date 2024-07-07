package com.microsoft.xal.browser

import android.os.Bundle
import android.util.Log
import com.microsoft.xal.browser.BrowserLaunchActivity.Companion.END_URL
import com.microsoft.xal.browser.BrowserLaunchActivity.Companion.IN_PROC_BROWSER
import com.microsoft.xal.browser.BrowserLaunchActivity.Companion.REQUEST_HEADER_KEYS
import com.microsoft.xal.browser.BrowserLaunchActivity.Companion.REQUEST_HEADER_VALUES
import com.microsoft.xal.browser.BrowserLaunchActivity.Companion.SHOW_TYPE
import com.microsoft.xal.browser.BrowserLaunchActivity.Companion.START_URL

class BrowserLaunchParameters private constructor(
    var startUrl: String,
    var endUrl: String,
    var requestHeaderKeys: Array<String>,
    var requestHeaderValues: Array<String>,
    var showType: ShowUrlType,
    var useInProcBrowser: Boolean
) {
    init {
        if (showType == ShowUrlType.NonAuthFlow) {
            Log.i(TAG, "BrowserLaunchParameters() Forcing inProc browser because flow is marked non-auth.")
        } else {
            if (requestHeaderKeys.isNotEmpty()) {
                Log.i(TAG, "BrowserLaunchParameters() Forcing inProc browser because request headers were found.")
            }
        }
        useInProcBrowser = true
    }

    companion object {
        private const val TAG = "BrowserLaunchActivity"

        fun parameters(bundle: Bundle): BrowserLaunchParameters? {
            val startUrl = bundle.getString(START_URL)
            val endUrl = bundle.getString(END_URL)
            val requestHeaderKeys = bundle.getStringArray(REQUEST_HEADER_KEYS)
            val requestHeaderValues = bundle.getStringArray(REQUEST_HEADER_VALUES)
            val showType = bundle[SHOW_TYPE] as ShowUrlType
            val useInProcBrowser = bundle.getBoolean(IN_PROC_BROWSER)
            if (startUrl == null || endUrl == null || requestHeaderKeys == null || requestHeaderValues == null || requestHeaderKeys.size != requestHeaderValues.size) {
                return null
            }
            return BrowserLaunchParameters(
                startUrl,
                endUrl,
                requestHeaderKeys,
                requestHeaderValues,
                showType,
                useInProcBrowser
            )
        }
    }
}
