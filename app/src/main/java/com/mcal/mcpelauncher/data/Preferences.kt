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
package com.mcal.mcpelauncher.data

import androidx.preference.PreferenceManager
import com.mcal.mcpelauncher.ModdedPEApplication

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
object Preferences {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(ModdedPEApplication.getContext())

    @JvmStatic
    val xHookSkyColor: Boolean
        get() = preferences.getBoolean("xhook_sky_color", false)

    @JvmStatic
    var rated: Boolean
        get() = preferences.getBoolean("isRated", false)
        set(mode) {
            preferences.edit().putBoolean("isRated", mode).apply()
        }

    @JvmStatic
    var isNightMode: Boolean
        get() = preferences.getBoolean("night_mode", false)
        set(value) {
            preferences.edit().putBoolean("night_mode", value).apply()
        }

    @JvmStatic
    val isBackgroundMusic: Boolean
        get() = preferences.getBoolean("background_music", false)
    
    @JvmStatic
    val isDesktopGui: Boolean
        get() = preferences.getBoolean("desktop_gui", false)

    @JvmStatic
    var isSafeMode: Boolean
        get() = preferences.getBoolean("safe_mode", false)
        set(flag) {
            preferences.edit().putBoolean("safe_mode", flag).apply()
        }

    @JvmStatic
    var languageType: Int
        get() = preferences.getInt("first_loaded", 0)
        set(i) {
            preferences.edit().putInt("first_loaded", i).apply()
        }

    @JvmStatic
    var dataSavedPath: String?
        get() = preferences.getString("data_saved_path", "default")
        set(str) {
            preferences.edit().putString("data_saved_path", str).apply()
        }

    @JvmStatic
    val minecraftPEPackageName: String?
        get() = preferences.getString("pkg_name", "com.mojang.minecraftpe")

    @JvmStatic
    fun setMinecraftPackageName(str: String?) {
        preferences.edit().putString("pkg_name", str).apply()
    }

    @JvmStatic
    var openGameFailed: String?
        get() = preferences.getString("open_game_failed_msg", null)
        set(str) {
            preferences.edit().putString("open_game_failed_msg", str).apply()
        }
}