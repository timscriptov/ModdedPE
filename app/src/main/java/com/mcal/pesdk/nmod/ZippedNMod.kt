/*
 * Copyright (C) 2018-2019 Тимашков Иван
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
package com.mcal.pesdk.nmod

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mcal.pesdk.ABIInfo
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

class ZippedNMod @Throws(IOException::class)
internal constructor(packageName: String, thisContext: Context, file: File) : NMod(packageName, thisContext) {
    private lateinit var mZipFile: ZipFile
    private lateinit var mFilePath: File
    override var assets: AssetManager? = null
        private set

    override val nModType: Int
        get() = NMod.Companion.NMOD_TYPE_ZIPPED

    override val isSupportedABI: Boolean
        get() = false

    override val packageResourcePath: String
        get() = mFilePath!!.path

    private val nativeLibsPath: String
        get() = NModFilePathManager(mContext).nModLibsDir.toString() + File.separator + packageName

    init {
        this.mZipFile = ZipFile(file)
        this.mFilePath = file

        if (mZipFile.getEntry(NMod.MANIFEST_NAME) == null)
            throw FileNotFoundException(NMod.MANIFEST_NAME)

        try {
            assets = AssetManager::class.java.newInstance()
        } catch (e: InstantiationException) {
        } catch (e: IllegalAccessException) {
        }

        try {
            val method = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
            method.invoke(assets, file.path)
        } catch (e: NoSuchMethodException) {
        } catch (e: SecurityException) {
        } catch (e: InvocationTargetException) {
        } catch (e: IllegalAccessException) {
        } catch (e: IllegalArgumentException) {
        }

        preload()
    }

    @Throws(IOException::class)
    override fun copyNModFiles(): NMod.NModPreloadBean {
        val ret = NMod.NModPreloadBean()
        val zipInput = ZipInputStream(BufferedInputStream(FileInputStream(mFilePath!!.absolutePath)))
        var entry: ZipEntry? = null

        File(nativeLibsPath).mkdirs()
        while ({ entry = zipInput.nextEntry; entry }() != null) {
            if (!entry!!.isDirectory && entry!!.name.startsWith("lib" + File.separator + ABIInfo.targetABIType + File.separator)) {
                val libInputStream = mZipFile.getInputStream(entry)
                var byteRead: Int = 0
                val buffer = ByteArray(1024)
                val outFile = File(nativeLibsPath + File.separator + entry!!.name.substring(entry!!.name.lastIndexOf(File.separator) + 1))
                outFile.createNewFile()
                val writerStream = FileOutputStream(outFile)
                while ({ byteRead = libInputStream.read(buffer); byteRead }() != -1) {
                    writerStream.write(buffer, 0, byteRead)
                }
                libInputStream.close()
                writerStream.close()
            }

        }

        zipInput.close()

        val nativeLibs = ArrayList<NModLibInfo>()
        if (info != null && info?.native_libs_info != null) {
            for (lib_item in info?.native_libs_info!!) {
                val newInfo = NModLibInfo()
                newInfo.name = nativeLibsPath + File.separator + lib_item.name
                newInfo.use_api = lib_item.use_api
                nativeLibs.add(newInfo)
            }
        }

        ret.native_libs = nativeLibs.toTypedArray()
        ret.assets_path = packageResourcePath
        return ret
    }

    public override fun createIcon(): Bitmap? {
        val imageStream: InputStream
        try {
            val iconEntry = mZipFile.getEntry("icon.png") ?: return null
            imageStream = mZipFile.getInputStream(iconEntry)
            return BitmapFactory.decodeStream(imageStream)
        } catch (e: IOException) {
            return null
        }

    }

    override fun createInfoInputStream(): InputStream? {
        try {
            val entry = mZipFile!!.getEntry(MANIFEST_NAME) ?: return null
            return mZipFile.getInputStream(entry)
        } catch (e: IOException) {
            return null
        }
    }
}