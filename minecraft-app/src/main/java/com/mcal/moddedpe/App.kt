package com.mcal.moddedpe

import androidx.multidex.MultiDexApplication
import com.google.android.material.color.DynamicColors

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}