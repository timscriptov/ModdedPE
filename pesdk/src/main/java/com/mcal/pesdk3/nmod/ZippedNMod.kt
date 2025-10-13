/*
 * Copyright (C) 2018-2025 Тимашков Иван
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
package com.mcal.pesdk3.nmod

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mcal.pesdk3.MinecraftInfo
import com.mcal.pesdk3.data.NModLibInfo
import com.mcal.pesdk3.data.NModPreloadBean
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class ZippedNMod(
    packageName: String,
    thisContext: Context,
    private val file: File
) : NMod(packageName, thisContext) {

    private val minecraftInfo = MinecraftInfo(thisContext)
    private val zipFile = ZipFile(file)
    private var assets = AssetManager::class.java.getDeclaredConstructor().newInstance()

    init {
        if (zipFile.getEntry(MANIFEST_NAME) == null) {
            throw FileNotFoundException(MANIFEST_NAME)
        }

        try {
            val method = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
            method.invoke(assets, file.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        preload()
    }

    @Throws(IOException::class)
    override fun copyNModFiles(): NModPreloadBean {
        val ret = NModPreloadBean()
        val zipInput = ZipInputStream(BufferedInputStream(FileInputStream(file.absolutePath)))

        val dexFiles = mutableListOf<String>()
        var entry: ZipEntry?

        while (zipInput.nextEntry.also { entry = it } != null) {
            entry?.let { currentEntry ->
                when {
                    !currentEntry.isDirectory && currentEntry.name.startsWith("lib${File.separator}${minecraftInfo.getMinecraftABI()}${File.separator}") -> {
                        extractNativeLibrary(currentEntry)
                    }

                    !currentEntry.isDirectory && isDexFile(currentEntry.name) -> {
                        val dexFilePath = extractDexFile(currentEntry)
                        dexFilePath?.let { dexFiles.add(it) }
                    }
                }
            }
        }
        zipInput.close()

        val nativeLibs = ArrayList<NModLibInfo>()
        mInfo?.nativeLibsInfo?.forEach { libItem ->
            val newInfo = NModLibInfo(
                useApi = libItem.useApi,
                name = getNativeLibsDir().path + File.separator + libItem.name
            )
            nativeLibs.add(newInfo)
        }

        ret.nativeLibs = nativeLibs.toTypedArray()
        ret.assetsPath = getPackageResourcePath()
        return ret
    }

    private fun extractNativeLibrary(entry: ZipEntry) {
        try {
            val libInputStream = zipFile.getInputStream(entry)
            val buffer = ByteArray(1024)
            val outFile = File(getNativeLibsDir(), entry.name.substringAfterLast(File.separator))
            outFile.createNewFile()
            FileOutputStream(outFile).use { writerStream ->
                var byteRead: Int
                while (libInputStream.read(buffer).also { byteRead = it } != -1) {
                    writerStream.write(buffer, 0, byteRead)
                }
            }
            libInputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun extractDexFile(entry: ZipEntry): String? {
        return try {
            val dexInputStream = zipFile.getInputStream(entry)
            val buffer = ByteArray(1024)
            val outFile = File(getDexesDir(), entry.name.substringAfterLast(File.separator))
            outFile.createNewFile()
            FileOutputStream(outFile).use { writerStream ->
                var byteRead: Int
                while (dexInputStream.read(buffer).also { byteRead = it } != -1) {
                    writerStream.write(buffer, 0, byteRead)
                }
            }
            dexInputStream.close()
            outFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun isDexFile(entryName: String): Boolean {
        return entryName == "classes.dex" ||
                entryName.matches(Regex("classes[0-9]+\\.dex")) ||
                entryName.matches(Regex("classes[0-9]*\\.dex"))
    }

    override fun getNModType(): Int {
        return NMOD_TYPE_ZIPPED
    }

    override fun isSupportedABI(): Boolean {
        return false
    }

    override fun getAssets(): AssetManager {
        return assets
    }

    override fun getPackageResourcePath(): String {
        return file.path
    }

    override fun createIcon(): Bitmap? {
        return try {
            val iconEntry = zipFile.getEntry("icon.png") ?: return null
            zipFile.getInputStream(iconEntry).use { imageStream ->
                BitmapFactory.decodeStream(imageStream)
            }
        } catch (e: IOException) {
            null
        }
    }

    override fun createInfoInputStream(): InputStream? {
        return try {
            val entry = zipFile.getEntry(MANIFEST_NAME) ?: return null
            zipFile.getInputStream(entry)
        } catch (e: IOException) {
            null
        }
    }
}
