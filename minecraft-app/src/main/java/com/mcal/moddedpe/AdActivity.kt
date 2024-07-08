package com.mcal.moddedpe

import android.os.Bundle
import android.view.KeyEvent
import com.mcal.moddedpe.utils.Helper
import com.mojang.minecraftpe.MainActivity
import com.wortise.ads.AdError
import com.wortise.ads.WortiseSdk
import com.wortise.ads.consent.ConsentManager.requestIfRequired
import com.wortise.ads.interstitial.InterstitialAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class AdActivity : MainActivity() {
    private var mInterstitial: InterstitialAd? = null
    private var mBackInterstitial: InterstitialAd? = null

    private var isFirstShown: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Helper.isNetworkConnected(this)) {
            WortiseSdk.initialize(this, App.AD_UNIT_WORTISE_ID) {
                requestIfRequired(this)
                loadDelay()
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (nativeKeyHandler(event.keyCode, event.action)) {
            when (event.action) {
                KeyEvent.ACTION_DOWN -> {
                    showInterstitialAd()
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        mInterstitial?.destroy()
        mBackInterstitial?.destroy()
    }

    private fun loadDelay() {
        CoroutineScope(Dispatchers.IO).launch {
            repeat(Int.MAX_VALUE) {
                if (isFirstShown) {
                    delay(15 * 1000)
                    isFirstShown = false
                } else {
                    delay(180 * 1000)
                }
                showInterstitialAd()
            }
        }
    }

    private fun showInterstitialAd() {
        mInterstitial = InterstitialAd(this, App.AD_UNIT_WORTISE_ID).also { interstitial ->
            interstitial.loadAd()
            interstitial.listener = object : InterstitialAd.Listener {
                override fun onInterstitialClicked(ad: InterstitialAd) {
                }

                override fun onInterstitialDismissed(ad: InterstitialAd) {
                }

                override fun onInterstitialFailedToLoad(ad: InterstitialAd, error: AdError) {
                    interstitial.destroy()
                }

                override fun onInterstitialFailedToShow(ad: InterstitialAd, error: AdError) {
                }

                override fun onInterstitialImpression(ad: InterstitialAd) {
                }

                override fun onInterstitialLoaded(ad: InterstitialAd) {
                    interstitial.showAd(this@AdActivity)
                }

                override fun onInterstitialShown(ad: InterstitialAd) {
                }
            }
        }
    }
}
