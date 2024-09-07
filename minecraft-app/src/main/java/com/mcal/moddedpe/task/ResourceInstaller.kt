package com.mcal.moddedpe.task

import android.content.Context
import androidx.preference.PreferenceManager
import com.mcal.moddedpe.BuildConfig
import com.mcal.moddedpe.utils.FileHelper
import com.mcal.moddedpe.utils.ZipHelper
import java.io.File

class ResourceInstaller(
    private val context: Context
) {
    fun install() {
        if (!isInstalled()) {
            extract("resources/worlds.zip", "games/com.mojang/minecraftWorlds")
            extract("resources/behavior_packs.zip", "games/com.mojang/behavior_packs")
            extract("resources/resource_packs.zip", "games/com.mojang/resource_packs")
            extract("resources/skin_packs.zip", "games/com.mojang/skin_packs")
            setIsInstalled(true)
        }
    }

    private fun extract(assetsName: String, output: String) {
        try {
            val tmp = File.createTempFile("temp", ".zip")
            val outputDir = File(context.filesDir.parentFile, output)
            context.assets.open(assetsName).use {
                FileHelper.writeToFile(tmp, it)
            }
            ZipHelper.extractFiles(tmp, outputDir)
            tmp.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isInstalled(): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("installed_resources_" + BuildConfig.VERSION_CODE, false)
    }

    private fun setIsInstalled(mode: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean("installed_resources_" + BuildConfig.VERSION_CODE, mode).apply()
    }
}
