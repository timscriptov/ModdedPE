package com.mcal.core.data

import android.content.Context
import java.io.File

object StorageHelper {
    const val VERSION = "1.20.0.01"

    fun resourcePackFile(context: Context): File {
        return File(resourcesDir(context), "resource_pack_$VERSION.zip")
    }

    fun behaviorPackFile(context: Context): File {
        return File(resourcesDir(context), "behavior_pack_$VERSION.zip")
    }

    fun mainPackFile(context: Context): File {
        return File(resourcesDir(context), "main_pack_$VERSION.zip")
    }

    fun nativeLibrariesFile(context: Context): File {
        return File(resourcesDir(context), "libraries_$VERSION.zip")
    }

    fun vanillaResourcePackFile(context: Context): File {
        return File(resourcesDir(context), "resource_pack_vanilla_$VERSION.zip")
    }

    fun libMinecraftPEFile(context: Context): File {
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
        val libDir = File(context.filesDir, "resources")
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