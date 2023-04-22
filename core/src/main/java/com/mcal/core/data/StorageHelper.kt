package com.mcal.core.data

import android.content.Context
import java.io.File

object StorageHelper {
    const val VERSION = "1.19.71.02"

    fun resourcePackFile(context: Context): File {
        return File(resourcesDir(context), "resource_pack_$VERSION.zip")
    }

    fun behaviorPackFile(context: Context): File {
        return File(resourcesDir(context), "behavior_pack_$VERSION.zip")
    }

    fun mainPackFile(context: Context): File {
        return File(resourcesDir(context), "main_pack_$VERSION.zip")
    }

    fun getNativeLibrariesFile(context: Context): File {
        return File(resourcesDir(context), "libraries.zip")
    }

    fun getLibMinecraftPEFile(context: Context): File {
        return File(nativeDir(context), "libminecraftpe.so")
    }

    fun nativeDir(context: Context): File {
        val libDir = File(context.filesDir, "native")
        if (!libDir.exists()) {
            libDir.mkdirs()
        }
        return libDir
    }

    private fun resourcesDir(context: Context): File {
        val libDir = File(context.cacheDir, "resources")
        if (!libDir.exists()) {
            libDir.mkdirs()
        }
        return libDir
    }

    fun minecraftPEDir(context: Context): File {
        val minecraftPEDir = File(context.filesDir.parent, "games/com.mojang/minecraftpe/")
        if (!minecraftPEDir.exists()) {
            minecraftPEDir.mkdirs()
        }
        return minecraftPEDir
    }
}