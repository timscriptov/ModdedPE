package com.mcal.moddedpe.ads.wortise

import android.os.Bundle
import android.view.KeyEvent
import com.mcal.moddedpe.App
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

open class WortiseAdActivity : MainActivity() {
    private var mInterstitial: InterstitialAd? = null
    private var isFirstShown: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Helper.isNetworkConnected(this)) {
            WortiseSdk.initialize(this, App.AD_UNIT_ID) {
                requestIfRequired(this)
                loadDelay()
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (nativeKeyHandler(event.keyCode, event.action) && event.action == KeyEvent.ACTION_DOWN) {
            showInterstitialAd()
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        mInterstitial?.destroy()
    }

    private fun loadDelay() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                delay(if (isFirstShown) App.FIRST_SHOW_AD_TIME else App.SHOW_AD_TIME)
                isFirstShown = false
                showInterstitialAd()
            }
        }
    }

    private fun showInterstitialAd() {
        mInterstitial = InterstitialAd(this, App.AD_UNIT_INTERSTITIAL_ID).apply {
            loadAd()
            listener = object : InterstitialAd.Listener {
                override fun onInterstitialClicked(ad: InterstitialAd) {}
                override fun onInterstitialDismissed(ad: InterstitialAd) {}
                override fun onInterstitialFailedToLoad(ad: InterstitialAd, error: AdError) {
                    destroy()
                }

                override fun onInterstitialFailedToShow(ad: InterstitialAd, error: AdError) {}
                override fun onInterstitialImpression(ad: InterstitialAd) {}
                override fun onInterstitialLoaded(ad: InterstitialAd) {
                    showAd(this@WortiseAdActivity)
                }

                override fun onInterstitialShown(ad: InterstitialAd) {}
            }
        }
    }
}
