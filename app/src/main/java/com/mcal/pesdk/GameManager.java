/*
 * Copyright (C) 2018-2020 Тимашков Иван
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
package com.mcal.pesdk;

import android.content.res.AssetManager;
import android.os.Bundle;

import com.google.gson.Gson;
import com.mcal.mcpelauncher.data.Constants;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.pesdk.nativeapi.NativeUtils;
import com.mcal.pesdk.nmod.NModLib;
import com.mcal.pesdk.utils.AssetOverrideManager;
import com.mojang.minecraftpe.MainActivity;

import org.jetbrains.annotations.NotNull;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class GameManager {
    private PESdk mPESdk;

    GameManager(PESdk pesdk) {
        mPESdk = pesdk;
    }

    public AssetManager getAssets() {
        return mPESdk.getMinecraftInfo().getAssets();
    }

    public void onMinecraftActivityCreate(@NotNull MainActivity activity, Bundle savedInstanceState) {
        boolean safeMode = Preferences.isSafeMode();
        AssetOverrideManager.addAssetOverride(activity.getAssets(), mPESdk.getMinecraftInfo().getMinecraftPackageContext().getPackageResourcePath());

        if (!safeMode) {
            NativeUtils.setValues(activity);
            Gson gson = new Gson();
            Bundle data = activity.getIntent().getExtras();

            Preloader.NModPreloadData preloadData = gson.fromJson(data.getString(Constants.NMOD_DATA_TAG), Preloader.NModPreloadData.class);

            for (String assetsPath : preloadData.assets_packs_path)
                AssetOverrideManager.addAssetOverride(activity.getAssets(), assetsPath);

            String[] loadedNModLibs = preloadData.loaded_libs;
            for (String nativeLibName : loadedNModLibs) {
                NModLib lib = new NModLib(nativeLibName);
                lib.callOnActivityCreate(activity, savedInstanceState);
            }
        }
    }

    public void onMinecraftActivityFinish(MainActivity activity) {
        if (Preferences.isSafeMode()) {
            return;
        }
        Gson gson = new Gson();
        Preloader.NModPreloadData preloadData = gson.fromJson(activity.getIntent().getExtras().getString(Constants.NMOD_DATA_TAG), Preloader.NModPreloadData.class);

        String[] loadedNModLibs = preloadData.loaded_libs;
        for (String nativeLibName : loadedNModLibs) {
            NModLib lib = new NModLib(nativeLibName);
            lib.callOnActivityFinish(activity);
        }
    }
}