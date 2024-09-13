package com.mcal.moddedpe.ads.ironsource

import android.util.Log
import android.view.View
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.sdk.LevelPlayBannerListener

class DemoBannerAdListener : LevelPlayBannerListener {
    private val TAG = DemoBannerAdListener::class.java.name

    /**
    Called after each banner ad has been successfully loaded, either a manual load or banner refresh
    @param adInfo The info of the ad.
     */
    override fun onAdLoaded(adInfo: AdInfo) {
        Log.e(TAG, "adInfo = $adInfo")
    }

    /**
    Called after a banner has attempted to load an ad but failed.
    This delegate will be sent both for manual load and refreshed banner failures.
    @param ironSourceError The reason for the error.
     */
    override fun onAdLoadFailed(ironSourceError: IronSourceError) {
        Log.e(TAG, "error = $ironSourceError")
    }

    /**
    Called after a banner has been clicked.
    @param adInfo The info of the ad.
     */
    override fun onAdClicked(adInfo: AdInfo) {
        Log.e(TAG, "adInfo = $adInfo")
    }

    /**
    Called when a user was taken out of the application context.
    @param adInfo The info of the ad.
     */
    override fun onAdLeftApplication(adInfo: AdInfo) {
        Log.e(TAG, "adInfo = $adInfo")
    }

    /**
    Called when a banner presented a full screen content.
    @param adInfo The info of the ad.
     */
    override fun onAdScreenPresented(adInfo: AdInfo) {
        Log.e(TAG, "adInfo = $adInfo")
    }

    /**
    Called after a full screen content has been dismissed.
    @param adInfo The info of the ad.
     */
    override fun onAdScreenDismissed(adInfo: AdInfo) {
        Log.e(TAG, "adInfo = $adInfo")
    }
}