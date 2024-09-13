package com.mcal.moddedpe.ads.ironsource

import android.util.Log
import com.ironsource.mediationsdk.sdk.InitializationListener

class DemoInitializationListener : InitializationListener {
    private val TAG = DemoInitializationListener::class.java.name

    /**
    Called after the Mediation successfully completes its initialization
     */
    override fun onInitializationComplete() {
        Log.e(TAG, "")
    }
}
