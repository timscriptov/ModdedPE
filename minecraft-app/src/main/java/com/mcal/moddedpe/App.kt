package com.mcal.moddedpe

import android.app.Application
import android.content.Context
import android.content.Intent
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.mcal.moddedpe.di.LauncherModules
import com.mcal.moddedpe.navigation.launcherScreenModule
import com.wortise.ads.AdSettings
import com.wortise.ads.WortiseSdk
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        WortiseSdk.initialize(this, AD_UNIT_ID)
        AdSettings.testEnabled = BuildConfig.DEBUG
        startKoin {
            androidContext(this@App)
            val featureModules = listOf(
                LauncherModules.modules,
            ).flatten()
            modules(featureModules)
        }
        ScreenRegistry {
            launcherScreenModule()
        }
    }

    companion object {
        const val AD_UNIT_ID = "f839a705-cf8f-4d43-88fa-42014c6fc886"
        const val AD_UNIT_INTERSTITIAL_ID = "b667b65a-7523-4562-8b40-b2d413c8e3de"
        const val FIRST_SHOW_AD_TIME = 15L * 1000L // 15 sec
        const val SHOW_AD_TIME = 180L * 1000L // 3 min

        // Change url for redirect minecraft urls
        const val REDIRECT_URL = "https://t.me/apkeditorproofficial"

        val REDIRECT_MINECRAFT_URLS = listOf(
            "https://aka.ms/privacy",
            "https://aka.ms/mcedulogs",
            "https://aka.ms/meeterms",
            "https://aka.ms/MinecraftAndroidExternalStorage",
        )

        // Change game privacy police
        const val PRIVACY_POLICE_GAME = "https://github.com/timscriptov/ModdedPE/tree/master/PrivacyPolicy/README.md"
        const val PRIVACY_POLICE_MINECRAFT = "https://account.mojang.com/terms#privacy"
        const val PRIVACY_POLICE_XBOX = "https://privacy.microsoft.com/en-us/privacystatement"

        fun restartApp(context: Context) {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
