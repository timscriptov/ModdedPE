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
package com.mcal.moddedpe3.data.repository

import android.content.Context
import com.mcal.pesdk.NModPreferences

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {
    private val nModPreferences = NModPreferences(context)

    override fun getSafeMode(): Boolean {
        return nModPreferences.safeMode
    }

    override fun setSafeMode(mode: Boolean) {
        nModPreferences.safeMode = mode
    }

    override fun getMinecraftPackageName(): String {
        return nModPreferences.minecraftPackageName
    }

    override fun setMinecraftPackageName(packageName: String) {
        nModPreferences.minecraftPackageName = packageName
    }
}
