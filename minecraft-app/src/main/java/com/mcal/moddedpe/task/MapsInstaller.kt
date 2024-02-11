package com.mcal.moddedpe.task

import android.content.Context
import com.mcal.moddedpe.utils.FileHelper
import com.mcal.moddedpe.utils.ZipHelper
import java.io.File

class MapsInstaller(
    private val context: Context
) {
    fun install() {
        try {
            val tmp = File(context.cacheDir, "worlds.zip")
            val worldsDir =
                File(context.filesDir.parentFile, "games/com.mojang/minecraftWorlds")
            if (!worldsDir.exists()) {
                worldsDir.mkdirs()
            }
            context.assets.open("resources/worlds.zip").use {
                FileHelper.writeToFile(tmp, it)
            }
            ZipHelper.unzip(tmp, worldsDir)
            tmp.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
