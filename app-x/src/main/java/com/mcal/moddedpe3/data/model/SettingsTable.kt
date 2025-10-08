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
package com.mcal.moddedpe3.data.model

import org.jetbrains.exposed.sql.Table

object SettingsTable : Table("settings") {
    val id = integer("id").autoIncrement()
    val isSafeMode = bool("is_safe_mode")
    val minecraftPackageName = varchar("minecraft_package_name", 255)

    override val primaryKey = PrimaryKey(id)
}
