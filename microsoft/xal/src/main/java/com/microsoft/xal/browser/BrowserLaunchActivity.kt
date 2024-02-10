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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.microsoft.xal.browser.BrowserSelector.selectBrowser
import com.microsoft.xal.browser.ShowUrlType.Companion.fromInt

/**
 * 13.08.2022
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
class BrowserLaunchActivity : AppCompatActivity() {
    private var mLaunchParameters: BrowserLaunchParameters? = null
    private var mOperationId: Long = 0
    private var mCustomTabsInProgress = false
    private var mSharedBrowserUsed = false
    private var mBrowserInfo: String? = null

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        val extras = intent.extras
        if (!checkNativeCodeLoaded()) {
            startActivity(
                applicationContext.packageManager.getLaunchIntentForPackage(
                    applicationContext.packageName
                )
            )
            finish()
        } else if (bundle != null) {
            mOperationId = bundle.getLong(OPERATION_ID_STATE_KEY)
            mCustomTabsInProgress = bundle.getBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY)
            mSharedBrowserUsed = bundle.getBoolean(SHARED_BROWSER_USED_STATE_KEY)
            mBrowserInfo = bundle.getString(BROWSER_INFO_STATE_KEY)
        } else if (extras != null) {
            mOperationId = extras.getLong(OPERATION_ID, 0L)
            val parameters =
                BrowserLaunchParameters.parameters(extras).also { mLaunchParameters = it }
            if (parameters != null && mOperationId != 0L) {
                return
            }
            finishOperation(WebResult.FAIL, null)
        } else if (intent.data != null) {
            setResult(RESULT_FAILED)
            finishOperation(WebResult.FAIL, null)
        } else {
            setResult(RESULT_FAILED)
            finishOperation(WebResult.FAIL, null)
        }
    }

    override fun onResume() {
        super.onResume()
        val z = mCustomTabsInProgress
        val browserLaunchParameters = mLaunchParameters
        if (!z && browserLaunchParameters != null) {
            mLaunchParameters = null
            startAuthSession(browserLaunchParameters)
        } else if (z) {
            mCustomTabsInProgress = false
            val data = intent.data
            if (data != null) {
                finishOperation(WebResult.SUCCESS, data.toString())
                return
            }
            finishOperation(WebResult.CANCEL, null)
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
        bundle.apply {
            putLong(OPERATION_ID_STATE_KEY, mOperationId)
            putBoolean(CUSTOM_TABS_IN_PROGRESS_STATE_KEY, mCustomTabsInProgress)
            putBoolean(SHARED_BROWSER_USED_STATE_KEY, mSharedBrowserUsed)
            putString(BROWSER_INFO_STATE_KEY, mBrowserInfo)
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    @Deprecated("Deprecated in Java")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == WEB_KIT_WEB_VIEW_REQUEST) {
            if (resultCode == RESULT_OK) {
                intent?.extras?.getString(WebKitWebViewController.RESPONSE_KEY, "")
                    ?.takeIf {
                        it.isNotEmpty()
                    }?.let {
                        finishOperation(WebResult.SUCCESS, it)
                        return
                    }
            } else if (resultCode == RESULT_CANCELED) {
                finishOperation(WebResult.CANCEL, null)
                return
            }
            finishOperation(WebResult.FAIL, null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isFinishing || mOperationId == 0L) {
            return
        }
        finishOperation(WebResult.CANCEL, null)
    }

    private fun startAuthSession(browserLaunchParameters: BrowserLaunchParameters) {
        val selectBrowser =
            selectBrowser(applicationContext, browserLaunchParameters.UseInProcBrowser)
        mBrowserInfo = selectBrowser.toString()
        val packageName = selectBrowser.packageName()
        if (packageName == null) {
            startWebView(
                browserLaunchParameters.StartUrl,
                browserLaunchParameters.EndUrl,
                browserLaunchParameters.ShowType,
                browserLaunchParameters.RequestHeaderKeys,
                browserLaunchParameters.RequestHeaderValues
            )
            return
        }
        startCustomTabsInBrowser(
            packageName,
            browserLaunchParameters.StartUrl,
            browserLaunchParameters.EndUrl,
            browserLaunchParameters.ShowType
        )
    }

    private fun startCustomTabsInBrowser(
        packageName: String,
        startUrl: String,
        endUrl: String,
        showUrlType: ShowUrlType?,
    ) {
        if (showUrlType === ShowUrlType.CookieRemovalSkipIfSharedCredentials) {
            finishOperation(WebResult.SUCCESS, endUrl)
            return
        }
        mCustomTabsInProgress = true
        mSharedBrowserUsed = true
        startActivity(CustomTabsIntent.Builder().apply {
            setShowTitle(true)
        }.build().apply {
            intent.data = Uri.parse(startUrl)
            intent.setPackage(packageName)
        }.intent)
    }

    private fun startWebView(
        startUrl: String,
        endUrl: String,
        showUrlType: ShowUrlType?,
        requestHeaderKeys: Array<String>,
        requestHeaderValues: Array<String>,
    ) {
        mSharedBrowserUsed = false
        startActivityForResult(
            Intent(
                applicationContext,
                WebKitWebViewController::class.java
            ).apply {
                putExtras(Bundle().apply {
                    putString(START_URL, startUrl)
                    putString(END_URL, endUrl)
                    putSerializable(SHOW_TYPE, showUrlType)
                    putStringArray(REQUEST_HEADER_KEYS, requestHeaderKeys)
                    putStringArray(REQUEST_HEADER_VALUES, requestHeaderValues)
                })
            }, WEB_KIT_WEB_VIEW_REQUEST
        )
    }

    private fun finishOperation(webResult: WebResult, finalUrl: String?) {
        val operationId = mOperationId
        mOperationId = 0L
        finish()
        if (operationId == 0L) {
            return
        }
        when (XalWebResult.mWebResult[webResult.ordinal]) {
            1 -> {
                urlOperationSucceeded(operationId, finalUrl, mSharedBrowserUsed, mBrowserInfo)
            }

            2 -> {
                urlOperationCanceled(operationId, mSharedBrowserUsed, mBrowserInfo)
            }

            3 -> {
            }

            else -> {
                urlOperationFailed(operationId, mSharedBrowserUsed, mBrowserInfo)
            }
        }
    }

    private fun checkNativeCodeLoaded(): Boolean {
        return try {
            checkIsLoaded()
            true
        } catch (unused: UnsatisfiedLinkError) {
            false
        }
    }

    enum class WebResult {
        SUCCESS, FAIL, CANCEL
    }

    class BrowserLaunchParameters private constructor(
        val StartUrl: String,
        val EndUrl: String,
        val RequestHeaderKeys: Array<String>,
        val RequestHeaderValues: Array<String>,
        val ShowType: ShowUrlType?,
        useInProcBrowser: Boolean
    ) {
        var UseInProcBrowser = true

        companion object {
            fun parameters(bundle: Bundle): BrowserLaunchParameters? {
                val startUrl = bundle.getString(START_URL)
                val endUrl = bundle.getString(END_URL)
                val headerKeys = bundle.getStringArray(REQUEST_HEADER_KEYS)
                val headerValues = bundle.getStringArray(REQUEST_HEADER_VALUES)
                val showUrlType = bundle[SHOW_TYPE] as ShowUrlType?
                val z = bundle.getBoolean(IN_PROC_BROWSER)
                return if (startUrl == null || endUrl == null || headerKeys == null || headerValues == null || headerKeys.size != headerValues.size) {
                    null
                } else BrowserLaunchParameters(
                    startUrl,
                    endUrl,
                    headerKeys,
                    headerValues,
                    showUrlType,
                    z
                )
            }
        }
    }

    companion object {
        const val END_URL = "END_URL"
        const val IN_PROC_BROWSER = "IN_PROC_BROWSER"
        const val OPERATION_ID = "OPERATION_ID"
        const val REQUEST_HEADER_KEYS = "REQUEST_HEADER_KEYS"
        const val REQUEST_HEADER_VALUES = "REQUEST_HEADER_VALUES"
        const val RESULT_FAILED = 8052
        const val SHOW_TYPE = "SHOW_TYPE"
        const val START_URL = "START_URL"
        const val WEB_KIT_WEB_VIEW_REQUEST = 8053
        private const val BROWSER_INFO_STATE_KEY = "BROWSER_INFO_STATE"
        private const val CUSTOM_TABS_IN_PROGRESS_STATE_KEY = "CUSTOM_TABS_IN_PROGRESS_STATE"
        private const val OPERATION_ID_STATE_KEY = "OPERATION_ID_STATE"
        private const val SHARED_BROWSER_USED_STATE_KEY = "SHARED_BROWSER_USED_STATE"

        @JvmStatic
        private external fun checkIsLoaded()

        @JvmStatic
        private external fun urlOperationCanceled(
            operationId: Long,
            sharedBrowserUsed: Boolean,
            browserInfo: String?
        )

        @JvmStatic
        private external fun urlOperationFailed(
            operationId: Long,
            sharedBrowserUsed: Boolean,
            browserInfo: String?
        )

        @JvmStatic
        private external fun urlOperationSucceeded(
            operationId: Long,
            finalUrl: String?,
            sharedBrowserUsed: Boolean,
            browserInfo: String?
        )

        @JvmStatic
        fun showUrl(
            operationId: Long,
            context: Context,
            startUrl: String,
            endUrl: String,
            showTypeInt: Int,
            requestHeaderKeys: Array<String?>,
            requestHeaderValues: Array<String?>,
            useInProcBrowser: Boolean
        ) {
            if (startUrl.isNotEmpty() && endUrl.isNotEmpty()) {
                val fromInt = fromInt(showTypeInt)
                if (fromInt == null) {
                    urlOperationFailed(operationId, false, null)
                    return
                } else if (requestHeaderKeys.size != requestHeaderValues.size) {
                    urlOperationFailed(operationId, false, null)
                    return
                } else {
                    context.startActivity(Intent(context, BrowserLaunchActivity::class.java).apply {
                        putExtras(Bundle().apply {
                            putLong(OPERATION_ID, operationId)
                            putString(START_URL, startUrl)
                            putString(END_URL, endUrl)
                            putSerializable(SHOW_TYPE, fromInt)
                            putStringArray(REQUEST_HEADER_KEYS, requestHeaderKeys)
                            putStringArray(REQUEST_HEADER_VALUES, requestHeaderValues)
                            putBoolean(IN_PROC_BROWSER, useInProcBrowser)
                        })
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    return
                }
            }
            urlOperationFailed(operationId, false, null)
        }
    }
}
