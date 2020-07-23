package com.mcal.mcpelauncher.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.mcal.mcpelauncher.ModdedPEApplication;


public final class Preferences {
    private static SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ModdedPEApplication.getContext());

    public Preferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(ModdedPEApplication.getContext());
    }

    public static boolean isBackgroundMusic() {
        return preferences.getBoolean("background_music", false);
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
        return preferences.getString("pkg_name", "default");
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
