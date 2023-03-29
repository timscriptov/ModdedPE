/*
 * Copyright (C) 2018-2021 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.pesdk.utils

import android.annotation.SuppressLint
import android.content.Context
import com.mcal.pesdk.utils.ABIInfo.ABI
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 * @author Vologhat
 */
@SuppressLint("StaticFieldLeak")
class SplitParser(private var context: Context) {
    private val minecraftLibs = arrayOf(
        "libminecraftpe.so",
        "libc++_shared.so",
        "libfmod.so",
        "libMediaDecoders_Android.so"
    )

    /**
     * Извлечение C++ библиотек из Minecraft
     */
    fun parseMinecraft() {
        val abi = "/lib/$ABI"
        val abiPath = File(context.cacheDir.path + abi)
        if (!abiPath.exists()) abiPath.mkdirs()
        try {
            val mcAppInfo = MinecraftInfo.getMinecraftPackageContext().applicationInfo
            if (isAppBundle() && mcAppInfo != null) {
                val splitPath = mutableListOf(*mcAppInfo.splitPublicSourceDirs)[0]
                val buffer = ByteArray(2048)
                for (so in minecraftLibs) {
                    val input = ZipFile(splitPath).getInputStream(ZipEntry("$abi$so"))
                    val fos = FileOutputStream("$abiPath/$so")
                    do {
                        val numRead = input.read(buffer)
                        if (numRead <= 0) {
                            break
                        }
                        fos.write(buffer, 0, numRead)
                    } while (true)
                    fos.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isAppBundle(): Boolean {
        return !MinecraftInfo.getMinecraftPackageContext().applicationInfo.splitPublicSourceDirs.isNullOrEmpty()
    }
}