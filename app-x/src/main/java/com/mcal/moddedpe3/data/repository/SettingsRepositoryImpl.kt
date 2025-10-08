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
import com.mcal.moddedpe3.data.model.SettingsScreenState
import com.mcal.moddedpe3.data.model.SettingsTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    private val databaseHelper by lazy { DatabaseHelper(context) }

    override suspend fun saveSettings(settings: SettingsScreenState) = withContext(Dispatchers.IO) {
        databaseHelper.execute { db ->
            SettingsTable.deleteAll()
            SettingsTable.insert {
                it[isSafeMode] = settings.isSafeMode
                it[minecraftPackageName] = settings.minecraftPackageName
            }
        }
    }

    override suspend fun loadSettings(): SettingsScreenState? = withContext(Dispatchers.IO) {
        databaseHelper.execute { db ->
            SettingsTable.selectAll().firstOrNull()?.let { row ->
                SettingsScreenState(
                    isSafeMode = row[SettingsTable.isSafeMode],
                    minecraftPackageName = row[SettingsTable.minecraftPackageName]
                )
            }
        }
    }

    override suspend fun getSafeMode(): Boolean = withContext(Dispatchers.IO) {
        loadSettings()?.isSafeMode ?: false
    }

    override suspend fun getMinecraftPackageName(): String = withContext(Dispatchers.IO) {
        loadSettings()?.minecraftPackageName ?: "com.mojang.minecraftpe"
    }
}
