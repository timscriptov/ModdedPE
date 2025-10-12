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
package com.mcal.pesdk3

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import com.mcal.pesdk3.data.LoadFailedException
import com.mcal.pesdk3.data.NModPreloadBean
import com.mcal.pesdk3.nativeapi.LibraryLoader
import com.mcal.pesdk3.nmod.NMod
import com.mcal.pesdk3.nmod.NModAPI
import com.mcal.pesdk3.nmod.NModJSONEditor
import com.mcal.pesdk3.nmod.NModLib
import com.mcal.pesdk3.nmod.NModTextEditor
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.ArrayList

class Preloader(
    private val context: Context,
    private var bundle: Bundle?,
    private var preloadListener: PreloadListener
) {
    companion object {
        const val NMOD_DATA_TAG = "nmod_data"
    }

    private var preloadData = NModPreloadData()
    private val assetsArrayList = ArrayList<String>()
    private val loadedNativeLibs = ArrayList<String>()
    private val loadedEnabledNMods = ArrayList<NMod>()
    private val nModAPI = NModAPI(context)
    private val preferences = NModPreferences(context)
    private val minecraftInfo = MinecraftInfo(context)
    private val json = Json { encodeDefaults = true }

    @Throws(PreloadException::class)
    fun preload() {
        preloadListener.onStart()

        if (bundle == null) {
            bundle = Bundle()
        }

        val safeMode = preferences.safeMode

        try {
            preloadListener.onLoadGameLauncherLib()
            LibraryLoader.loadLauncher(minecraftInfo.getMinecraftPackageNativeLibraryDir()!!)
            if (!safeMode) {
                preloadListener.onLoadSubstrateLib()
                LibraryLoader.loadSubstrate()

                preloadListener.onLoadXHookLib()
                LibraryLoader.loadXHook()

                preloadListener.onLoadPESdkLib()
                LibraryLoader.loadNModAPI(minecraftInfo.getMinecraftPackageNativeLibraryDir()!!)
            }
        } catch (throwable: Throwable) {
            throw PreloadException(PreloadException.TYPE_LOAD_LIBS_FAILED, throwable)
        }

        if (!safeMode) {
            preloadListener.onStartLoadingAllNMods()
            // init data
            preloadData = NModPreloadData()
            assetsArrayList.clear()
            loadedNativeLibs.clear()
            loadedEnabledNMods.clear()

            assetsArrayList.add(minecraftInfo.getMinecraftPackageContext()!!.packageResourcePath)

            // init index
            val unIndexedNModArrayList = nModAPI.getImportedEnabledNMods()
            for (index in unIndexedNModArrayList.indices.reversed()) {
                loadedEnabledNMods.add(unIndexedNModArrayList[index])
            }

            // start init nmods
            for (nmod in loadedEnabledNMods) {
                if (nmod.isBugPack()) {
                    preloadListener.onFailedLoadingNMod(nmod)
                    continue
                }

                val preloadDataItem: NModPreloadBean
                try {
                    preloadDataItem = nmod.copyNModFiles()
                } catch (ioe: IOException) {
                    nmod.setBugPack(LoadFailedException(LoadFailedException.Companion.TYPE_IO_FAILED, ioe))
                    preloadListener.onFailedLoadingNMod(nmod)
                    continue
                }

                if (loadNMod(context, nmod, preloadDataItem)) {
                    preloadListener.onNModLoaded(nmod)
                } else {
                    preloadListener.onFailedLoadingNMod(nmod)
                }
            }

            preloadData.assetsPacksPath = assetsArrayList.toTypedArray()
            preloadData.loadedLibs = loadedNativeLibs.toTypedArray()
            bundle?.putString(NMOD_DATA_TAG, json.encodeToString(preloadData))
        } else {
            bundle?.putString(NMOD_DATA_TAG, json.encodeToString(NModPreloadData()))
        }

        preloadListener.onFinish(bundle)
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private fun loadNMod(context: Context, nmod: NMod, preloadDataItem: NModPreloadBean): Boolean {
        var jsonEditFile: String? = null
        var textEditFile: String? = null

        // edit json files
        if (nmod.getInfo()?.jsonEdit != null && nmod.getInfo()!!.jsonEdit!!.isNotEmpty()) {
            val assetFiles = ArrayList<File>()
            for (filePath in assetsArrayList) {
                assetFiles.add(File(filePath))
            }
            val jsonEditor = NModJSONEditor(context, nmod, assetFiles.toTypedArray())
            try {
                val outResourceFile = jsonEditor.edit()
                jsonEditFile = outResourceFile.absolutePath
            } catch (e: IOException) {
                nmod.setBugPack(
                    if (e is FileNotFoundException) {
                        LoadFailedException(LoadFailedException.Companion.TYPE_FILE_NOT_FOUND, e)
                    } else {
                        LoadFailedException(LoadFailedException.Companion.TYPE_IO_FAILED, e)
                    }
                )
                return false
            } catch (e: IllegalArgumentException) {
                nmod.setBugPack(LoadFailedException(LoadFailedException.Companion.TYPE_JSON_SYNTAX, e))
                return false
            }
        }

        // edit text files
        if (nmod.getInfo()?.textEdit != null && nmod.getInfo()!!.textEdit!!.isNotEmpty()) {
            val assetFiles = ArrayList<File>()
            for (filePath in assetsArrayList) {
                assetFiles.add(File(filePath))
            }
            val textEditor = NModTextEditor(context, nmod, assetFiles.toTypedArray())
            try {
                val outResourceFile = textEditor.edit()
                textEditFile = outResourceFile.absolutePath
            } catch (e: IOException) {
                nmod.setBugPack(
                    if (e is FileNotFoundException) {
                        LoadFailedException(LoadFailedException.Companion.TYPE_FILE_NOT_FOUND, e)
                    } else {
                        LoadFailedException(LoadFailedException.Companion.TYPE_IO_FAILED, e)
                    }
                )
                return false
            }
        }

        preloadDataItem.assetsPath?.let {
            assetsArrayList.add(it)
        }

        jsonEditFile?.let { assetsArrayList.add(it) }
        textEditFile?.let { assetsArrayList.add(it) }

        // load elf files
        if (preloadDataItem.nativeLibs != null && preloadDataItem.nativeLibs!!.isNotEmpty()) {
            for (nameItem in preloadDataItem.nativeLibs!!) {
                try {
                    System.load(nameItem.name!!)
                } catch (t: Throwable) {
                    nmod.setBugPack(LoadFailedException(LoadFailedException.Companion.TYPE_LOAD_LIB_FAILED, t))
                    return false
                }
            }

            for (nameItem in preloadDataItem.nativeLibs!!) {
                if (nameItem.useApi) {
                    val lib = NModLib(nameItem.name!!)
                    lib.callOnLoad(
                        minecraftInfo.getMinecraftVersionName(minecraftInfo.findMinecraftPackage()),
                        nModAPI.getVersionName()
                    )
                    loadedNativeLibs.add(nameItem.name!!)
                }
            }
        }
        return true
    }

    @Serializable
    data class NModPreloadData(
        var assetsPacksPath: Array<String> = emptyArray(),
        var loadedLibs: Array<String> = emptyArray()
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as NModPreloadData

            if (!assetsPacksPath.contentEquals(other.assetsPacksPath)) return false
            if (!loadedLibs.contentEquals(other.loadedLibs)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = assetsPacksPath.contentHashCode()
            result = 31 * result + loadedLibs.contentHashCode()
            return result
        }
    }

    open class PreloadListener {
        open fun onStart() {}
        open fun onLoadSubstrateLib() {}
        open fun onLoadXHookLib() {}
        open fun onLoadGameLauncherLib() {}
        open fun onLoadPESdkLib() {}
        open fun onStartLoadingAllNMods() {}
        open fun onNModLoaded(nmod: NMod) {}
        open fun onFailedLoadingNMod(nmod: NMod) {}
        open fun onFinish(bundle: Bundle?) {}
    }
}