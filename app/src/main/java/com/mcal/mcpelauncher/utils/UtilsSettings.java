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
package com.mcal.mcpelauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.mcal.pesdk.utils.LauncherOptions;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class UtilsSettings implements LauncherOptions {
    private final static String TAG_SETTINGS = "moddedpe_settings";
    private final static String TAG_SAFE_MODE = "safe_mode";
    private final static String TAG_FIRST_LOADED = "first_loaded";
    private final static String TAG_DATA_SAVED_PATH = "data_saved_path";
    private final static String TAG_PKG_NAME = "pkg_name";
    private final static String TAG_LANGUAGE = "language_type";
    private final static String TAG_OPEN_GAME_FAILED = "open_game_failed_msg";
    private Context mContext;

    public UtilsSettings(Context context) {
        this.mContext = context;
    }

    public boolean isSafeMode() {
        return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getBoolean(TAG_SAFE_MODE, false);
    }

    public void setSafeMode(boolean z) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(TAG_SAFE_MODE, z);
        editor.apply();
    }

    public boolean isFirstLoaded() {
        return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getBoolean(TAG_FIRST_LOADED, false);
    }

    public void setFirstLoaded(boolean z) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(TAG_FIRST_LOADED, z);
        editor.apply();
    }

    public int getLanguageType() {
        return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getInt(TAG_LANGUAGE, 0);
    }

    public void setLanguageType(int z) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putInt(TAG_LANGUAGE, z);
        editor.apply();
    }

    @Override
    public String getDataSavedPath() {
        return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getString(TAG_DATA_SAVED_PATH, STRING_VALUE_DEFAULT);
    }

    public void setDataSavedPath(String z) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(TAG_DATA_SAVED_PATH, z);
        editor.apply();
    }

    @Override
    public String getMinecraftPEPackageName() {
        return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getString(TAG_PKG_NAME, STRING_VALUE_DEFAULT);
    }

    public void setMinecraftPackageName(String z) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(TAG_PKG_NAME, z);
        editor.apply();
    }

    public String getOpenGameFailed() {
        return mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getString(TAG_OPEN_GAME_FAILED, null);
    }

    public void setOpenGameFailed(String z) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(TAG_OPEN_GAME_FAILED, z);
        editor.apply();
    }
}
