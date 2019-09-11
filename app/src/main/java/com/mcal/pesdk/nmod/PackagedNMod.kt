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
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class PackagedNMod internal constructor(packageName: String, contextThiz: Context, private val packageContext: Context) : NMod(packageName, contextThiz) {

    override val packageResourcePath: String
        get() = packageContext.packageResourcePath

    override val isSupportedABI: Boolean
        get() = false

    override val nModType: Int
        get() = NMod.NMOD_TYPE_PACKAGED

    private val nativeLibsPath: String
        get() = packageContext.applicationInfo.nativeLibraryDir

    override val assets: AssetManager
        get() = packageContext.assets

    init {
        preload()
    }

    override fun copyNModFiles(): NModPreloadBean {
        val ret = NModPreloadBean()
        ret.assets_path = packageResourcePath
        val nativeLibs = ArrayList<NModLibInfo>()
        if (info?.native_libs_info != null) {
            for (lib_item in info?.native_libs_info!!) {
                val newInfo = NModLibInfo()
                newInfo.name = nativeLibsPath + File.separator + lib_item.name
                newInfo.use_api = lib_item.use_api
                nativeLibs.add(newInfo)
            }
        }
        ret.native_libs = nativeLibs.toTypedArray()
        return ret
    }

    public override fun createIcon(): Bitmap? {
        try {
            val packageManager = packageContext.packageManager
            val packageInfo = packageManager.getPackageInfo(packageContext.packageName, 0)
            val iconRes = packageInfo.applicationInfo.icon
            return BitmapFactory.decodeResource(packageContext.resources, iconRes)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }

    override fun createInfoInputStream(): InputStream? {
        return try {
            assets.open(MANIFEST_NAME)
        } catch (e: IOException) {
            null
        }
    }
}