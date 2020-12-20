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
package com.mcal.mcpelauncher.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.mcal.mcpelauncher.ModdedPEApplication;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public final class Preferences {
    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModdedPEApplication.getContext());

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(ModdedPEApplication.getContext());
    }

    public static boolean getXHookSkyColor() {
        return preferences.getBoolean("xhook_sky_color", false);
    }

    public static boolean getRated() {
        return preferences.getBoolean("isRated", false);
    }

    public static void setRated(boolean mode) {
        preferences.edit().putBoolean("isRated", mode).apply();
    }

    public static boolean isNightMode() {
        return preferences.getBoolean("night_mode", false);
    }

    public static void setNightMode(boolean value) {
        preferences.edit().putBoolean("night_mode", value).apply();
    }

    public static boolean isBackgroundMusic() {
        return preferences.getBoolean("background_music", false);
    }

    public static boolean DesktopGui() {
        return preferences.getBoolean("desktop_gui", false);
    }

    public static boolean isSafeMode() {
        return preferences.getBoolean("safe_mode", false);
    }

    public static void setSafeMode(boolean flag) {
        preferences.edit().putBoolean("safe_mode", flag).apply();
    }

    public static boolean isFirstLoaded() {
        return preferences.getBoolean("first_loaded", false);
    }

    public static void setFirstLoaded(boolean flag) {
        preferences.edit().putBoolean("first_loaded", flag).apply();
    }

    public static int getLanguageType() {
        return preferences.getInt("first_loaded", 0);
    }

    public static void setLanguageType(int i) {
        preferences.edit().putInt("first_loaded", i).apply();
    }

    public static String getDataSavedPath() {
        return preferences.getString("data_saved_path", "default");
    }

    public static void setDataSavedPath(String str) {
        preferences.edit().putString("data_saved_path", str).apply();
    }

    public static String getMinecraftPEPackageName() {
        return preferences.getString("pkg_name", "com.mojang.minecraftpe");
    }

    public static void setMinecraftPackageName(String str) {
        preferences.edit().putString("pkg_name", str).apply();
    }

    public static String getOpenGameFailed() {
        return preferences.getString("open_game_failed_msg", null);
    }

    public static void setOpenGameFailed(String str) {
        preferences.edit().putString("open_game_failed_msg", str).apply();
    }
}
