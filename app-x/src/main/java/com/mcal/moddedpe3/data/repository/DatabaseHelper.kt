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
import com.mcal.moddedpe3.data.model.SettingsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

class DatabaseHelper(private val context: Context) {

    private val databaseName = "app_settings.db"

    fun getDatabase(): Database {
        val databaseFile = File(context.filesDir, databaseName)
        return Database.connect(
            "jdbc:sqlite:${databaseFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )
    }

    fun <T> execute(block: (Database) -> T): T {
        val database = getDatabase()
        return transaction(database) {
            SchemaUtils.create(SettingsTable)
            block(database)
        }
    }

    init {
        try {
            Class.forName("org.sqlite.JDBC")
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("SQLite JDBC driver not found", e)
        }
    }
}