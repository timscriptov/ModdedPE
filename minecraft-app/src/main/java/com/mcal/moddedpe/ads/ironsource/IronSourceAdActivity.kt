package com.mcal.moddedpe.ads.ironsource

import android.content.Intent
import android.content.res.Resources.getSystem
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import com.ironsource.mediationsdk.ISBannerSize
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.integration.IntegrationHelper
import com.ironsource.sdk.controller.m
import com.mcal.moddedpe.App
import com.mcal.moddedpe.BuildConfig
import com.mojang.minecraftpe.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min


open class IronSourceAdActivity : MainActivity() {
    companion object {
        private const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469
        private const val APP_KEY = "85460dcd"

        private val Int.dp: Int get() = (this / getSystem().displayMetrics.density).toInt()
        private val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()
    }

    private var isFirstShown: Boolean = true

    private var popupHeight = 0
    private var isShowed = false
    private var popupWidth = 0
    private var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupIronSourceSdk()
        initBanner()
        checkPermission()
        loadInterstitial()
        loadDelay()
    }

    private fun initBanner() {
        val size = ISBannerSize.BANNER
        val view = IronSource.createBanner(this, size)
        popupWidth = size.width
        popupHeight = size.height
        popupWindow = PopupWindow(this).apply {
            width = popupWidth.px
            height = popupHeight.px
            isOutsideTouchable = true
            isTouchable = true
            isFocusable = true
            contentView = view
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                windowLayoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                }
            }
        }
        IronSource.loadBanner(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                checkPermission()
            } else {
                showBanner()
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            } else {
                showBanner()
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (nativeKeyHandler(
                event.keyCode,
                event.action
            ) && event.action == KeyEvent.ACTION_DOWN
        ) {
            showInterstitial() // If user clicks hard BACK show ads
        }
        return super.dispatchKeyEvent(event)
    }

    private fun loadDelay() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(if (isFirstShown) App.FIRST_SHOW_AD_TIME else App.SHOW_AD_TIME)
                isFirstShown = false
                runOnUiThread { showInterstitial() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        IronSource.onResume(this)
        showBanner()
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(this)
        hideBanner()
    }

    private fun setupIronSourceSdk() {
        if (BuildConfig.DEBUG) {
            IntegrationHelper.validateIntegration(this)
        }

        IronSource.setLevelPlayRewardedVideoListener(DemoRewardedVideoAdListener())
        IronSource.setLevelPlayInterstitialListener(DemoInterstitialAdListener())
        IronSource.addImpressionDataListener(DemoImpressionDataListener())

        IronSource.init(this, APP_KEY, DemoInitializationListener())
    }

    fun showRewardedVideo() {
        if (IronSource.isRewardedVideoAvailable()) {
            IronSource.showRewardedVideo()
        }
    }

    private fun loadInterstitial() {
        IronSource.loadInterstitial()
    }

    private fun showInterstitial() {
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial()
        }
    }

    private fun showBanner() {
        if (!isShowed && !isFinishing) {
            isShowed = true
            runOnUiThread {
                val metrics = DisplayMetrics()
                val screenHeight = min(metrics.widthPixels.toDouble(), metrics.heightPixels.toDouble()).toInt()
                val screenWidth = max(metrics.widthPixels.toDouble(), metrics.heightPixels.toDouble()).toInt()
                popupWindow?.showAtLocation(
                    window.decorView,
                    Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                    (screenWidth - popupWidth) / 2,
                    0
                )
            }
        }
    }

    private fun hideBanner() {
        if (isShowed) {
            isShowed = false
            runOnUiThread {
                popupWindow?.dismiss()
            }
        }
    }
}
