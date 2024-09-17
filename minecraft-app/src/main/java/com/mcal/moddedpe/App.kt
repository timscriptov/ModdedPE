package com.mcal.moddedpe

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.mcal.moddedpe.di.LauncherModules
import com.mcal.moddedpe.navigation.launcherScreenModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
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
    }
}
