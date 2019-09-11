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
package com.mcal.pesdk

import android.content.res.AssetManager
import android.os.Bundle

import com.google.gson.Gson
import com.mcal.pesdk.nativeapi.NativeUtils
import com.mcal.pesdk.nmod.NModLib
import com.mcal.pesdk.utils.AssetOverrideManager
import com.mojang.minecraftpe.MainActivity

class GameManager internal constructor(//##################################################################
        private val mPESdk: PESdk) {

    val isSafeMode: Boolean
        get() = mPESdk.launcherOptions.isSafeMode

    val assets: AssetManager
        get() = mPESdk.minecraftInfo.assets

    fun onMinecraftActivityCreate(activity: MainActivity, savedInstanceState: Bundle) {
        val safeMode = mPESdk.launcherOptions.isSafeMode
        AssetOverrideManager.addAssetOverride(activity.assets, mPESdk.minecraftInfo.minecraftPackageContext.packageResourcePath)

        if (!safeMode) {
            NativeUtils.setValues(activity, mPESdk.launcherOptions)
            val gson = Gson()
            val data = activity.intent.extras

            val preloadData = gson.fromJson(data!!.getString(PreloadingInfo.NMOD_DATA_TAG), Preloader.NModPreloadData::class.java)

            for (assetsPath in preloadData.assets_packs_path!!)
                AssetOverrideManager.addAssetOverride(activity.assets, assetsPath)

            val loadedNModLibs = preloadData.loaded_libs
            for (nativeLibName in loadedNModLibs!!) {
                val lib = NModLib(nativeLibName)
                lib.callOnActivityCreate(activity, savedInstanceState)
            }
        }
    }

    fun onMinecraftActivityFinish(activity: MainActivity) {
        if (mPESdk.launcherOptions.isSafeMode)
            return
        val gson = Gson()
        val preloadData = gson.fromJson(activity.intent.extras!!.getString(PreloadingInfo.NMOD_DATA_TAG), Preloader.NModPreloadData::class.java)

        val loadedNModLibs = preloadData.loaded_libs
        for (nativeLibName in loadedNModLibs!!) {
            val lib = NModLib(nativeLibName)
            lib.callOnActivityFinish(activity)
        }
    }
}
