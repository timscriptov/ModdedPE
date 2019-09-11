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

import android.content.Context
import android.os.Bundle
import com.google.gson.Gson
import com.mcal.pesdk.nativeapi.LibraryLoader
import com.mcal.pesdk.nmod.*
import org.json.JSONException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class Preloader @JvmOverloads constructor(private val mPESdk: PESdk, private var mBundle: Bundle?, listener: PreloadListener? = null) {
    private var mPreloadListener: PreloadListener? = null
    private var mPreloadData = NModPreloadData()
    private var mAssetsArrayList = ArrayList<String>()
    private var mLoadedNativeLibs = ArrayList<String>()
    private var mLoadedEnabledNMods = ArrayList<NMod>()

    init {
        mPreloadListener = listener
        if (mPreloadListener == null)
            mPreloadListener = PreloadListener()
    }

    @Throws(PreloadException::class)
    fun preload(context: Context) {
        mPreloadListener!!.onStart()

        if (mBundle == null)
            mBundle = Bundle()
        val gson = Gson()
        val safeMode = mPESdk.launcherOptions.isSafeMode

        try {
            mPreloadListener!!.onLoadNativeLibs()
            mPreloadListener!!.onLoadSubstrateLib()
            LibraryLoader.loadSubstrate()
            mPreloadListener!!.onLoadFModLib()
            LibraryLoader.loadFMod(mPESdk.minecraftInfo.minecraftPackageNativeLibraryDir)
            mPreloadListener!!.onLoadMinecraftPELib()
            LibraryLoader.loadMinecraftPE(mPESdk.minecraftInfo.minecraftPackageNativeLibraryDir)
            mPreloadListener!!.onLoadGameLauncherLib()
            LibraryLoader.loadLauncher(mPESdk.minecraftInfo.minecraftPackageNativeLibraryDir)
            if (!safeMode) {
                mPreloadListener!!.onLoadPESdkLib()
                LibraryLoader.loadNModAPI(mPESdk.minecraftInfo.minecraftPackageNativeLibraryDir)
            }
            mPreloadListener!!.onFinishedLoadingNativeLibs()
        } catch (throwable: Throwable) {
            throw PreloadException(PreloadException.TYPE_LOAD_LIBS_FAILED, throwable)
        }

        if (!safeMode) {
            mPreloadListener!!.onStartLoadingAllNMods()
            //init data
            mPreloadData = NModPreloadData()
            mAssetsArrayList = ArrayList()
            mLoadedNativeLibs = ArrayList()
            mLoadedEnabledNMods = ArrayList()

            mAssetsArrayList.add(mPESdk.minecraftInfo.minecraftPackageContext.packageResourcePath)

            //init index
            val unIndexedNModArrayList = mPESdk.nModAPI.importedEnabledNMods
            for (index in unIndexedNModArrayList.indices.reversed()) {
                mLoadedEnabledNMods.add(unIndexedNModArrayList[index])
            }

            //start init nmods
            for (nmod in mLoadedEnabledNMods) {
                if (nmod.isBugPack) {
                    mPreloadListener!!.onFailedLoadingNMod(nmod)
                    continue
                }

                val preloadDataItem: NMod.NModPreloadBean
                try {
                    preloadDataItem = nmod.copyNModFiles()
                } catch (ioe: IOException) {
                    nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_IO_FAILED, ioe))
                    mPreloadListener!!.onFailedLoadingNMod(nmod)
                    continue
                }

                if (loadNMod(context, nmod, preloadDataItem))
                    mPreloadListener!!.onNModLoaded(nmod)
                else
                    mPreloadListener!!.onFailedLoadingNMod(nmod)
            }

            mPreloadData.assets_packs_path = mAssetsArrayList.toTypedArray()
            mPreloadData.loaded_libs = mLoadedNativeLibs.toTypedArray()
            mBundle!!.putString(PreloadingInfo.NMOD_DATA_TAG, gson.toJson(mPreloadData))
            mPreloadListener!!.onFinishedLoadingAllNMods()
        } else
            mBundle!!.putString(PreloadingInfo.NMOD_DATA_TAG, gson.toJson(Preloader.NModPreloadData()))

        mPreloadListener!!.onFinish(mBundle!!)
    }

    private fun loadNMod(context: Context, nmod: NMod, preloadDataItem: NMod.NModPreloadBean): Boolean {
        val minecraftInfo = mPESdk.minecraftInfo

        var jsonEditFile: String? = null
        var textEditFile: String? = null

        //edit json files
        if (nmod.info!!.json_edit != null && nmod.info!!.json_edit!!.size > 0) {
            val assetFiles = ArrayList<File>()
            for (filePath in mAssetsArrayList)
                assetFiles.add(File(filePath))
            val jsonEditor = NModJSONEditor(context, nmod, assetFiles.toTypedArray())
            try {
                val outResourceFile = jsonEditor.edit()
                jsonEditFile = outResourceFile.absolutePath
            } catch (e: IOException) {
                if (e is FileNotFoundException)
                    nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e))
                else
                    nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_IO_FAILED, e))
                return false
            } catch (jsonE: JSONException) {
                nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_JSON_SYNTAX, jsonE))
                return false
            }

        }
        //edit text files
        if (nmod.info!!.text_edit != null && nmod.info!!.text_edit!!.size > 0) {
            val assetFiles = ArrayList<File>()
            for (filePath in mAssetsArrayList)
                assetFiles.add(File(filePath))
            val textEditor = NModTextEditor(context, nmod, assetFiles.toTypedArray())
            try {
                val outResourceFile = textEditor.edit()
                textEditFile = outResourceFile.absolutePath
            } catch (e: IOException) {
                if (e is FileNotFoundException)
                    nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e))
                else
                    nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_IO_FAILED, e))
                return false
            }

        }

        if (preloadDataItem.assets_path != null)
            mAssetsArrayList.add(preloadDataItem.assets_path!!)

        if (jsonEditFile != null)
            mAssetsArrayList.add(jsonEditFile)
        if (textEditFile != null)
            mAssetsArrayList.add(textEditFile)

        //load elf files
        if (preloadDataItem.native_libs != null && preloadDataItem.native_libs!!.size > 0) {
            for (nameItem in preloadDataItem.native_libs!!) {
                try {
                    System.load(nameItem.name!!)
                } catch (t: Throwable) {
                    nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_LOAD_LIB_FAILED, t))
                    return false
                }

            }

            for (nameItem in preloadDataItem.native_libs!!) {
                if (nameItem.use_api) {
                    val lib = NModLib(nameItem.name!!)
                    lib.callOnLoad(minecraftInfo.minecraftVersionName!!, mPESdk.nModAPI.versionName)
                    mLoadedNativeLibs.add(nameItem.name!!)
                }
            }
        }
        return true
    }

    internal class NModPreloadData {
        var assets_packs_path: Array<String>? = null
        var loaded_libs: Array<String>? = null
    }

    open class PreloadListener {
        open fun onStart() {}

        open fun onLoadNativeLibs() {}

        open fun onLoadSubstrateLib() {}

        fun onLoadGameLauncherLib() {}

        open fun onLoadFModLib() {}

        open fun onLoadMinecraftPELib() {}

        open fun onLoadPESdkLib() {}

        open fun onFinishedLoadingNativeLibs() {}

        open fun onStartLoadingAllNMods() {}

        open fun onNModLoaded(nmod: NMod) {}

        open fun onFailedLoadingNMod(nmod: NMod) {}

        open fun onFinishedLoadingAllNMods() {}

        open fun onFinish(bundle: Bundle) {}
    }
}
