package com.mcal.moddedpe.data

import android.content.Context
import java.io.File

object StorageHelper {
    fun getMinecraftPeDir(context: Context): File {
        val minecraftPEDir = File(context.filesDir.parent, "games/com.mojang/minecraftpe/")
        if (!minecraftPEDir.exists()) {
            minecraftPEDir.mkdirs()
        }
        return minecraftPEDir
    }
}