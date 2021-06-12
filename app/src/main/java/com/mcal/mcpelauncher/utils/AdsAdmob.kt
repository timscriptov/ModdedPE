package com.mcal.mcpelauncher.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.appsflyer.internal.model.event.AdRevenue
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mcal.mcpelauncher.data.Constants

object AdsAdmob {
    private var interstitialAd: InterstitialAd? = null

    @JvmStatic
    fun loadInterestialAd(context: Context) {
        InterstitialAd.load(context, Constants.INTERESTIAL_AD_ID, AdRequest.Builder().build(),
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                interstitialAd = p0
                Log.d("AdsAdmob", "interstitialAd = p0")
            }
        })
    }

    @JvmStatic
    fun showInterestialAd(activity: Activity, callback: (() -> Unit)? = null) {
        if(interstitialAd != null) {
            interstitialAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    interstitialAd = null
                    callback?.invoke()
                    Log.d("AdsAdmob", "callback?.invoke()")
                }
            }
            interstitialAd!!.show(activity)
            Log.d("AdsAdmob", "interstitialAd!!.show(activity)")
        } else {
            callback?.invoke()
            Log.d("AdsAdmob", "callback?.invoke()")
        }
    }
}