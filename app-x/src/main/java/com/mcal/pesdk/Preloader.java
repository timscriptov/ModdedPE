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
package com.mcal.pesdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import com.google.gson.Gson;
import com.mcal.pesdk.nativeapi.LibraryLoader;
import com.mcal.pesdk.nmod.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Preloader {
    public final static String NMOD_DATA_TAG = "nmod_data";
    private Bundle mBundle;
    private PreloadListener mPreloadListener;
    private NModPreloadData mPreloadData = new NModPreloadData();
    private ArrayList<String> mAssetsArrayList = new ArrayList<>();
    private ArrayList<String> mLoadedNativeLibs = new ArrayList<>();
    private ArrayList<NMod> mLoadedEnabledNMods = new ArrayList<>();
    private final Context context;
    private final NModAPI nModAPI;
    private NModPreferences preferences;
    private MinecraftInfo minecraftInfo;

    public Preloader(Context context, Bundle bundle, PreloadListener listener) {
        this.context = context;
        preferences = new NModPreferences(context);
        minecraftInfo = new MinecraftInfo(context);
        nModAPI = new NModAPI(context);
        nModAPI.initNModDatas();
        mBundle = bundle;
        mPreloadListener = listener;
        if (mPreloadListener == null)
            mPreloadListener = new PreloadListener();
    }

    public void preload() throws PreloadException {
        mPreloadListener.onStart();

        if (mBundle == null) {
            mBundle = new Bundle();
        }
        Gson gson = new Gson();
        boolean safeMode = preferences.getSafeMode();

        try {
            mPreloadListener.onLoadGameLauncherLib();
            LibraryLoader.loadLauncher(minecraftInfo.getMinecraftPackageNativeLibraryDir());
            if (!safeMode) {
                mPreloadListener.onLoadSubstrateLib();
                LibraryLoader.loadSubstrate();

                mPreloadListener.onLoadXHookLib();
                LibraryLoader.loadXHook();

                mPreloadListener.onLoadPESdkLib();
                LibraryLoader.loadNModAPI(minecraftInfo.getMinecraftPackageNativeLibraryDir());
            }
        } catch (Throwable throwable) {
            throw new PreloadException(PreloadException.TYPE_LOAD_LIBS_FAILED, throwable);
        }

        if (!safeMode) {
            mPreloadListener.onStartLoadingAllNMods();
            //init data
            mPreloadData = new NModPreloadData();
            mAssetsArrayList = new ArrayList<>();
            mLoadedNativeLibs = new ArrayList<>();
            mLoadedEnabledNMods = new ArrayList<>();

            mAssetsArrayList.add(minecraftInfo.getMinecraftPackageContext().getPackageResourcePath());

            //init index
            ArrayList<NMod> unIndexedNModArrayList = nModAPI.getImportedEnabledNMods();
            for (int index = unIndexedNModArrayList.size() - 1; index >= 0; --index) {
                mLoadedEnabledNMods.add(unIndexedNModArrayList.get(index));
            }

            //start init nmods
            for (NMod nmod : mLoadedEnabledNMods) {
                if (nmod.isBugPack()) {
                    mPreloadListener.onFailedLoadingNMod(nmod);
                    continue;
                }

                NMod.NModPreloadBean preloadDataItem;
                try {
                    preloadDataItem = nmod.copyNModFiles();
                } catch (IOException ioe) {
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_IO_FAILED, ioe));
                    mPreloadListener.onFailedLoadingNMod(nmod);
                    continue;
                }

                if (loadNMod(context, nmod, preloadDataItem))
                    mPreloadListener.onNModLoaded(nmod);
                else
                    mPreloadListener.onFailedLoadingNMod(nmod);
            }

            mPreloadData.assets_packs_path = mAssetsArrayList.toArray(new String[0]);
            mPreloadData.loaded_libs = mLoadedNativeLibs.toArray(new String[0]);
            mBundle.putString(NMOD_DATA_TAG, gson.toJson(mPreloadData));
        } else
            mBundle.putString(NMOD_DATA_TAG, gson.toJson(new NModPreloadData()));

        mPreloadListener.onFinish(mBundle);
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private boolean loadNMod(Context context, @NotNull NMod nmod, NMod.NModPreloadBean preloadDataItem) {
        String jsonEditFile = null;
        String textEditFile = null;

        //edit json files
        if (nmod.getInfo().json_edit != null && nmod.getInfo().json_edit.length > 0) {
            ArrayList<File> assetFiles = new ArrayList<>();
            for (String filePath : mAssetsArrayList)
                assetFiles.add(new File(filePath));
            NModJSONEditor jsonEditor = new NModJSONEditor(context, nmod, assetFiles.toArray(new File[0]));
            try {
                File outResourceFile = jsonEditor.edit();
                jsonEditFile = outResourceFile.getAbsolutePath();
            } catch (IOException e) {
                if (e instanceof FileNotFoundException)
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e));
                else
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_IO_FAILED, e));
                return false;
            } catch (JSONException jsonE) {
                nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_JSON_SYNTAX, jsonE));
                return false;
            }
        }
        //edit text files
        if (nmod.getInfo().text_edit != null && nmod.getInfo().text_edit.length > 0) {
            ArrayList<File> assetFiles = new ArrayList<>();
            for (String filePath : mAssetsArrayList)
                assetFiles.add(new File(filePath));
            NModTextEditor textEditor = new NModTextEditor(context, nmod, assetFiles.toArray(new File[0]));
            try {
                File outResourceFile = textEditor.edit();
                textEditFile = outResourceFile.getAbsolutePath();
            } catch (IOException e) {
                if (e instanceof FileNotFoundException)
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_FILE_NOT_FOUND, e));
                else
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_IO_FAILED, e));
                return false;
            }
        }

        if (preloadDataItem.assets_path != null)
            mAssetsArrayList.add(preloadDataItem.assets_path);

        if (jsonEditFile != null)
            mAssetsArrayList.add(jsonEditFile);
        if (textEditFile != null)
            mAssetsArrayList.add(textEditFile);

        //load elf files
        if (preloadDataItem.native_libs != null && preloadDataItem.native_libs.length > 0) {
            for (NMod.NModLibInfo nameItem : preloadDataItem.native_libs) {
                try {
                    System.load(nameItem.name);
                } catch (Throwable t) {
                    nmod.setBugPack(new LoadFailedException(LoadFailedException.TYPE_LOAD_LIB_FAILED, t));
                    return false;
                }
            }

            for (NMod.NModLibInfo nameItem : preloadDataItem.native_libs) {
                if (nameItem.use_api) {
                    NModLib lib = new NModLib(nameItem.name);
                    lib.callOnLoad(minecraftInfo.getMinecraftVersionName(minecraftInfo.findMinecraftPackage()), nModAPI.getVersionName());
                    mLoadedNativeLibs.add(nameItem.name);
                }
            }
        }
        return true;
    }

    public static class NModPreloadData {
        public String[] assets_packs_path;
        public String[] loaded_libs;
    }

    public static class PreloadListener {
        public void onStart() {
        }

        public void onLoadSubstrateLib() {
        }

        public void onLoadXHookLib() {
        }

        public void onLoadGameLauncherLib() {
        }

        public void onLoadPESdkLib() {
        }

        public void onStartLoadingAllNMods() {
        }

        public void onNModLoaded(NMod nmod) {
        }

        public void onFailedLoadingNMod(NMod nmod) {
        }

        public void onFinish(Bundle bundle) {
        }
    }
}
