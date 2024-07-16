package com.mcal.moddedpe

import android.app.Application
import android.content.Context
import com.google.android.material.color.DynamicColors
import com.mcal.moddedpe.task.CustomServers
import com.mcal.moddedpe.task.MapsInstaller
import com.mcal.moddedpe.task.NativeInstaller
import com.mcal.moddedpe.utils.ABIHelper
import com.mcal.moddedpe.utils.Patcher
import com.wortise.ads.AdSettings
import com.wortise.ads.WortiseSdk
import java.io.File

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CustomServers(this).install()
        MapsInstaller(this).install()
        NativeInstaller(this).install()
        patchNativeLibraryDir()
        loadLibraries()
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        WortiseSdk.initialize(this, AD_UNIT_ID)
        if (BuildConfig.DEBUG) {
            AdSettings.testEnabled = true
        }
    }

    private fun patchNativeLibraryDir() {
        runCatching {
            Patcher.patchNativeLibraryDir(classLoader, File("${filesDir}/native/${ABIHelper.getABI()}/"))
        }
    }

    private fun loadLibraries() {
        System.loadLibrary("fmod")
        arrayListOf(
            "c++_shared",
            "minecraftpe",
            "MediaDecoders_Android"
        ).forEach {
            runCatching {
                System.loadLibrary(it)
            }
        }
    }

    companion object {
        const val AD_UNIT_ID = ""
        const val AD_UNIT_INTERSTITIAL_ID = ""
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
    }
}
