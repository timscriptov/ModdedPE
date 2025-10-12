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
package com.mcal.pesdk3

import android.content.Context

class NModPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var safeMode: Boolean
        get() = sharedPreferences.getBoolean(KEY_SAFE_MODE, false)
        set(isSafeMode) {
            val editor = sharedPreferences.edit()
            editor.putBoolean(KEY_SAFE_MODE, isSafeMode)
            editor.apply()
        }

    var minecraftPackageName: String
        get() = sharedPreferences.getString(KEY_PACKAGE_NAME, "com.mojang.minecraftpe") ?: "com.mojang.minecraftpe"
        set(packageName) {
            val editor = sharedPreferences.edit()
            editor.putString(KEY_PACKAGE_NAME, packageName)
            editor.apply()
        }

    companion object {
        private const val PREF_NAME = "nmod_settings"
        private const val KEY_SAFE_MODE = "safe_mode"
        private const val KEY_PACKAGE_NAME = "package_name"
    }
}