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
package com.mcal.moddedpe3

import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import com.mcal.moddedpe3.data.repository.MainRepository
import com.mcal.moddedpe3.data.repository.SettingsRepository
import com.mcal.pesdk3.MinecraftInfo.Companion.MINECRAFT_LIBS
import com.mcal.pesdk3.Preloader.Companion.NMOD_DATA_TAG
import com.mcal.pesdk3.data.NModPreloadData
import com.mcal.pesdk3.dex.Patcher
import com.mcal.pesdk3.nmod.NModLib
import com.mojang.minecraftpe.MainActivity
import kotlinx.serialization.json.Json
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import java.io.File

class MinecraftActivity : MainActivity(), KoinComponent {

    private val repository: MainRepository by inject()
    private val settingsRepository: SettingsRepository by inject()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            // Запущен через ФМ для установки Майнкрафт аддонов
            if ((intent.extras?.getString(NMOD_DATA_TAG)) == null) {
                MINECRAFT_LIBS.forEach { library ->
                    repository.loadNativeLibrary(library)
                }
            }
            patchNativeDir()
            callOnActivityCreate(savedInstanceState)
            super.onCreate(savedInstanceState)
        } catch (e: Exception) {
            Log.e("MinecraftActivity", "Error during initialization", e)
            finish()
        }
    }

    private fun patchNativeDir() {
        val nativeDirPath = repository.getMinecraftPackageNativeLibraryDir()
        if (nativeDirPath == null) {
            Log.e("MinecraftActivity", "Failed to get native library directory")
            finish()
            return
        }

        val dir = File(nativeDirPath)
        if (!dir.exists()) {
            Log.e("MinecraftActivity", "Native library directory does not exist: $nativeDirPath")
            finish()
            return
        }

        Patcher.patchNativeLibraryDir(classLoader, nativeDirPath)
    }

    private fun callOnActivityCreate(savedInstanceState: Bundle?) {
        if (!settingsRepository.getSafeMode()) {
            intent.extras?.let { data ->
                val jsonString = data.getString(NMOD_DATA_TAG)
                if (!jsonString.isNullOrEmpty()) {
                    try {
                        val preloadData = json.decodeFromString<NModPreloadData>(jsonString)

                        for (assetsPath in preloadData.assetsPacksPath) {
                            repository.addAssetPath(getAssets(), assetsPath)
                        }

                        for (nativeLibName in preloadData.loadedLibs) {
                            val lib = NModLib(nativeLibName)
                            lib.callOnActivityCreate(this, savedInstanceState)
                        }
                    } catch (e: Exception) {
                        Log.e("MinecraftActivity", "Error parsing preload data", e)
                    }
                }
            }
        }
    }

    override fun getAssets(): AssetManager {
        try {
            repository.addAssetOverrides(super.getAssets())
        } catch (e: Exception) {
            Log.e("MinecraftActivity", "Error adding asset overrides", e)
        }
        return super.getAssets()
    }

    override fun getExternalStoragePath(): String {
        return this.filesDir.absolutePath
    }

    override fun onDestroy() {
        callOnActivityFinish()
        super.onDestroy()
    }

    private fun callOnActivityFinish() {
        intent.extras?.let { data ->
            val jsonString = data.getString(NMOD_DATA_TAG)
            if (!jsonString.isNullOrEmpty()) {
                try {
                    val preloadData = json.decodeFromString<NModPreloadData>(jsonString)

                    for (nativeLibName in preloadData.loadedLibs) {
                        val lib = NModLib(nativeLibName)
                        lib.callOnActivityFinish(this)
                    }
                } catch (e: Exception) {
                    Log.e("MinecraftActivity", "Error parsing preload data in onDestroy", e)
                }
            }
        }
    }
}
