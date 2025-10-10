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
import com.mojang.minecraftpe.MainActivity
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import java.io.File

class MinecraftActivity : MainActivity(), KoinComponent {

    private val repository: MainRepository by inject()

    override fun onCreate(p1: Bundle?) {
//         if (!safeMode) {
//            Gson gson = new Gson();
//            Bundle data = activity.getIntent().getExtras();
//
//            Preloader.NModPreloadData preloadData = gson.fromJson(data.getString(Constants.NMOD_DATA_TAG), Preloader.NModPreloadData.class);
//
//            for (String assetsPath : preloadData.assets_packs_path)
//                AssetOverrideManager.addAssetOverride(activity.getAssets(), assetsPath);
//
//            String[] loadedNModLibs = preloadData.loaded_libs;
//            for (String nativeLibName : loadedNModLibs) {
//                NModLib lib = new NModLib(nativeLibName);
//                lib.callOnActivityCreate(activity, savedInstanceState);
//            }
//        }
        try {
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

            Patcher.patchNativeLibraryDir(classLoader, dir)
            super.onCreate(p1)
        } catch (e: Exception) {
            Log.e("MinecraftActivity", "Error during initialization", e)
            finish()
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
//        val loadedNModLibs: Array<String?> = preloadData.loaded_libs
//        for (nativeLibName in loadedNModLibs) {
//            val lib = NModLib(nativeLibName)
//            lib.callOnActivityFinish(activity)
//        }
        super.onDestroy()
    }
}
