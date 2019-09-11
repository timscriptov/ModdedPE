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
package com.mcal.mcpelauncher.utils

import android.content.Context
import com.mcal.pesdk.utils.LauncherOptions

class UtilsSettings(private val mContext: Context) : LauncherOptions {
    var isFirstLoaded: Boolean
        get() = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getBoolean(TAG_FIRST_LOADED, false)
        set(z) {
            val editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit()
            editor.putBoolean(TAG_FIRST_LOADED, z)
            editor.apply()
        }

    var languageType: Int
        get() = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getInt(TAG_LANGUAGE, 0)
        set(z) {
            val editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit()
            editor.putInt(TAG_LANGUAGE, z)
            editor.apply()
        }

    var openGameFailed: String?
        get() = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getString(TAG_OPEN_GAME_FAILED, null)
        set(z) {
            val editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit()
            editor.putString(TAG_OPEN_GAME_FAILED, z)
            editor.apply()
        }

    override var isSafeMode: Boolean
        get() = mContext
                .getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE)
                .getBoolean(TAG_SAFE_MODE, false)
        set(value) {
            val editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit()
            editor.putBoolean(TAG_SAFE_MODE, value)
            editor.apply()
        }

    override var dataSavedPath: String
        get() = mContext
                .getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).getString(TAG_DATA_SAVED_PATH, LauncherOptions.STRING_VALUE_DEFAULT)
                .toString()
        set(value) {
            val editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit()
            editor.putString(TAG_DATA_SAVED_PATH, value)
            editor.apply()
        }

    override val minecraftPEPackageName: String
        get() = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE)
                .getString(TAG_PKG_NAME, LauncherOptions.STRING_VALUE_DEFAULT)
                .toString()

    fun setMinecraftPackageName(z: String) {
        val editor = mContext.getSharedPreferences(TAG_SETTINGS, Context.MODE_PRIVATE).edit()
        editor.putString(TAG_PKG_NAME, z)
        editor.apply()
    }

    companion object {
        private val TAG_SETTINGS = "moddedpe_settings"
        private val TAG_SAFE_MODE = "safe_mode"
        private val TAG_FIRST_LOADED = "first_loaded"
        private val TAG_DATA_SAVED_PATH = "data_saved_path"
        private val TAG_PKG_NAME = "pkg_name"
        private val TAG_LANGUAGE = "language_type"
        private val TAG_OPEN_GAME_FAILED = "open_game_failed_msg"
    }
}
