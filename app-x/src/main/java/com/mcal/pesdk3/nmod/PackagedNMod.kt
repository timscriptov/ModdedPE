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
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mcal.pesdk3.data.NModLibInfo
import com.mcal.pesdk3.data.NModPreloadBean
import java.io.File
import java.io.IOException
import java.io.InputStream

class PackagedNMod(
    packageName: String,
    contextThiz: Context,
    private val packageContext: Context
) : NMod(packageName, contextThiz) {

    init {
        preload()
    }

    override fun getPackageResourcePath(): String {
        return packageContext.packageResourcePath
    }

    override fun copyNModFiles(): NModPreloadBean {
        val ret = NModPreloadBean()
        ret.assetsPath = getPackageResourcePath()

        val nativeLibs = ArrayList<NModLibInfo>()
        mInfo?.nativeLibsInfo?.forEach { libItem ->
            val newInfo = NModLibInfo(
                useApi = libItem.useApi,
                name = getNativeLibsPath() + File.separator + libItem.name
            )
            nativeLibs.add(newInfo)
        }

        ret.nativeLibs = nativeLibs.toTypedArray()
        return ret
    }

    override fun isSupportedABI(): Boolean {
        return false
    }

    override fun getNModType(): Int {
        return NMOD_TYPE_PACKAGED
    }

    private fun getNativeLibsPath(): String {
        return packageContext.applicationInfo.nativeLibraryDir
    }

    override fun getAssets(): AssetManager {
        return packageContext.assets
    }

    override fun createIcon(): Bitmap? {
        return try {
            packageContext.packageManager.getPackageInfo(
                packageContext.packageName, 0
            ).applicationInfo?.icon?.let { iconRes ->
                BitmapFactory.decodeResource(packageContext.resources, iconRes)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    override fun createInfoInputStream(): InputStream? {
        return try {
            getAssets().open(MANIFEST_NAME)
        } catch (e: IOException) {
            null
        }
    }
}