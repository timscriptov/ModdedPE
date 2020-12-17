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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.gson.Gson;
import com.mcal.mcpelauncher.data.Preferences;
import com.mcal.pesdk.nativeapi.LibraryLoader;
import com.mcal.pesdk.nmod.LoadFailedException;
import com.mcal.pesdk.nmod.NMod;
import com.mcal.pesdk.nmod.NModJSONEditor;
import com.mcal.pesdk.nmod.NModLib;
import com.mcal.pesdk.nmod.NModTextEditor;
import com.mcal.pesdk.utils.MinecraftInfo;
import com.mcal.pesdk.utils.SplitParser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class Preloader {
    private Bundle mBundle;
    private PESdk mPESdk;
    private PreloadListener mPreloadListener;
    private NModPreloadData mPreloadData = new NModPreloadData();
    private ArrayList<String> mAssetsArrayList = new ArrayList<>();
    private ArrayList<String> mLoadedNativeLibs = new ArrayList<>();
    private ArrayList<NMod> mLoadedEnabledNMods = new ArrayList<>();

    public Preloader(PESdk pesdk, Bundle bundle, PreloadListener listener) {
        mBundle = bundle;
        mPreloadListener = listener;
        mPESdk = pesdk;
        if (mPreloadListener == null)
            mPreloadListener = new PreloadListener();
    }

    public Preloader(PESdk pesdk, Bundle bundle) {
        this(pesdk, bundle, null);
    }

    public void preload(Context context) throws PreloadException {
        mPreloadListener.onStart();

        if (mBundle == null)
            mBundle = new Bundle();
        Gson gson = new Gson();
        boolean safeMode = Preferences.isSafeMode();

        try {
            SplitParser.parse(context);

            mPreloadListener.onLoadNativeLibs();

            mPreloadListener.onLoadFModLib();
            LibraryLoader.loadFMod(mPESdk.getMinecraftInfo().getMinecraftPackageNativeLibraryDir());

            mPreloadListener.onLoadCppSharedLib();
            LibraryLoader.loadCppShared(mPESdk.getMinecraftInfo().getMinecraftPackageNativeLibraryDir());

            mPreloadListener.onLoadMinecraftPELib();
            LibraryLoader.loadMinecraftPE(mPESdk.getMinecraftInfo().getMinecraftPackageNativeLibraryDir());

            mPreloadListener.onLoadSubstrateLib();
            LibraryLoader.loadSubstrate();

            mPreloadListener.onLoadXHookLib();
            LibraryLoader.loadXHook();

            if(Preferences.getXHookSkyColor()) {
                mPreloadListener.onLoadXHookSkyColorLib();
                LibraryLoader.loadXHookSkyColor();
            }

            mPreloadListener.onLoadGameLauncherLib();
            LibraryLoader.loadLauncher(mPESdk.getMinecraftInfo().getMinecraftPackageNativeLibraryDir());
            if (!safeMode) {
                mPreloadListener.onLoadPESdkLib();
                LibraryLoader.loadNModAPI(mPESdk.getMinecraftInfo().getMinecraftPackageNativeLibraryDir());
            }
            mPreloadListener.onFinishedLoadingNativeLibs();
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

            mAssetsArrayList.add(mPESdk.getMinecraftInfo().getMinecraftPackageContext().getPackageResourcePath());

            //init index
            ArrayList<NMod> unIndexedNModArrayList = mPESdk.getNModAPI().getImportedEnabledNMods();
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
            mBundle.putString(PreloadingInfo.NMOD_DATA_TAG, gson.toJson(mPreloadData));
            mPreloadListener.onFinishedLoadingAllNMods();
        } else
            mBundle.putString(PreloadingInfo.NMOD_DATA_TAG, gson.toJson(new Preloader.NModPreloadData()));

        mPreloadListener.onFinish(mBundle);
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private boolean loadNMod(Context context, @NotNull NMod nmod, NMod.NModPreloadBean preloadDataItem) {
        MinecraftInfo minecraftInfo = mPESdk.getMinecraftInfo();

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
                    lib.callOnLoad(minecraftInfo.getMinecraftVersionName(), mPESdk.getNModAPI().getVersionName());
                    mLoadedNativeLibs.add(nameItem.name);
                }
            }
        }
        return true;
    }

    static class NModPreloadData {
        String[] assets_packs_path;
        String[] loaded_libs;
    }

    public static class PreloadListener {
        public void onStart() {
        }

        public void onLoadNativeLibs() {
        }

        public void onLoadSubstrateLib() {
        }

        public void onLoadXHookLib() {
        }

        public void onLoadXHookSkyColorLib() {
        }

        public void onLoadGameLauncherLib() {
        }

        public void onLoadFModLib() {
        }

        public void onLoadMinecraftPELib() {
        }

        public void onLoadCppSharedLib() {

        }

        public void onLoadPESdkLib() {
        }

        public void onFinishedLoadingNativeLibs() {
        }

        public void onStartLoadingAllNMods() {
        }

        public void onNModLoaded(NMod nmod) {
        }

        public void onFailedLoadingNMod(NMod nmod) {
        }

        public void onFinishedLoadingAllNMods() {
        }

        public void onFinish(Bundle bundle) {
        }
    }
}
