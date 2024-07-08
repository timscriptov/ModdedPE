package com.mcal.moddedpe

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.wortise.ads.AdSettings
import com.wortise.ads.WortiseSdk

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        WortiseSdk.initialize(this, AD_UNIT_WORTISE_ID)
        if (BuildConfig.DEBUG) {
            AdSettings.testEnabled = true
        }
    }

    companion object {
        const val AD_UNIT_WORTISE_ID = ""
    }
}
