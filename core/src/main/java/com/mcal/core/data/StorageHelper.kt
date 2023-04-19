package com.mcal.core.data

import android.content.Context
import java.io.File

object StorageHelper {
    val version = "1.19.71.02"

    fun resourcePackFile(context: Context): File {
        return File(resourcesDir(context), "resource_pack_$version.zip")
    }

    fun behaviorPackFile(context: Context): File {
        return File(resourcesDir(context), "behavior_pack_$version.zip")
    }

    fun mainPackFile(context: Context): File {
        return File(resourcesDir(context), "main_pack_$version.zip")
    }

    private fun resourcesDir(context: Context): File {
        val libDir = File(context.cacheDir, "resources")
        if (!libDir.exists()) {
            libDir.mkdirs()
        }
        return libDir
    }

    fun getTmpLibLokiCraftFile(context: Context): File {
        return File(context.cacheDir, "liblokicraft.zip")
    }

    fun nativeDir(context: Context): File {
        val libDir = File(context.filesDir, "native")
        if (!libDir.exists()) {
            libDir.mkdirs()
        }
        return libDir
    }
}