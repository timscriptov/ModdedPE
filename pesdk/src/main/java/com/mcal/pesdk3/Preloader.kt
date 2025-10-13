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
import com.mcal.pesdk3.data.NModPreloadData
import com.mcal.pesdk3.dex.Patcher
import com.mcal.pesdk3.nativeapi.LibraryLoader
import com.mcal.pesdk3.nmod.*
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

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
    private val loadedDexes = ArrayList<String>()
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
            minecraftInfo.getMinecraftPackageNativeLibraryDir()?.let { nativeDir ->
                preloadListener.onLoadGameLauncherLib()
                LibraryLoader.loadLauncher(nativeDir)
                if (!safeMode) {
                    preloadListener.onLoadSubstrateLib()
                    LibraryLoader.loadSubstrate()

                    preloadListener.onLoadXHookLib()
                    LibraryLoader.loadXHook()

                    preloadListener.onLoadPESdkLib()
                    LibraryLoader.loadNModAPI(nativeDir)
                }
            }
        } catch (throwable: Throwable) {
            throw PreloadException(PreloadException.TYPE_LOAD_LIBS_FAILED, throwable)
        }

        if (!safeMode) {
            preloadListener.onStartLoadingAllNMods()
            // init data
            assetsArrayList.clear()
            loadedNativeLibs.clear()
            loadedDexes.clear()
            loadedEnabledNMods.clear()

            minecraftInfo.getMinecraftPackageContext()?.let { mcPkgContext ->
                assetsArrayList.add(mcPkgContext.packageResourcePath)
            }

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
                    nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_IO_FAILED, ioe))
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
            preloadData.loadedDexes = loadedDexes.toTypedArray()
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
        val jsonEdit = nmod.getInfo()?.jsonEdit
        if (jsonEdit != null && jsonEdit.isNotEmpty()) {
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
                        LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e)
                    } else {
                        LoadFailedException(LoadFailedException.TYPE_IO_FAILED, e)
                    }
                )
                return false
            } catch (e: IllegalArgumentException) {
                nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_JSON_SYNTAX, e))
                return false
            }
        }

        // edit text files
        val textEdit = nmod.getInfo()?.textEdit
        if (textEdit != null && textEdit.isNotEmpty()) {
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
                        LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e)
                    } else {
                        LoadFailedException(LoadFailedException.TYPE_IO_FAILED, e)
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

        // Load DEX files
        try {
            val dexFiles = nmod.getAllDexes()
            if (dexFiles.isNotEmpty()) {
                val dexOptDir = context.codeCacheDir.absolutePath
                Patcher.patchMultipleDexFiles(
                    context.classLoader,
                    dexFiles,
                    dexOptDir
                )
                loadedDexes.addAll(dexFiles)
            }
        } catch (t: Throwable) {
            nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_LOAD_DEX_FAILED, t))
            return false
        }

        // load elf files
        val nativeLibs = preloadDataItem.nativeLibs
        if (nativeLibs != null && nativeLibs.isNotEmpty()) {
            for (nameItem in nativeLibs) {
                val name = nameItem.name
                if (name != null) {
                    try {
                        System.load(name)
                    } catch (t: Throwable) {
                        nmod.setBugPack(LoadFailedException(LoadFailedException.TYPE_LOAD_LIB_FAILED, t))
                        return false
                    }
                }
            }

            for (nameItem in nativeLibs) {
                if (nameItem.useApi) {
                    val name = nameItem.name
                    if (name != null) {
                        val lib = NModLib(name)
                        lib.callOnLoad(
                            minecraftInfo.getMinecraftVersionName(minecraftInfo.findMinecraftPackage()),
                            nModAPI.getVersionName()
                        )
                        loadedNativeLibs.add(name)
                    }
                }
            }
        }
        return true
    }
}
