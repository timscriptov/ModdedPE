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
package com.mcal.pesdk3.data

import kotlinx.serialization.Serializable

@Serializable
data class NModPreloadData(
    var assetsPacksPath: Array<String> = emptyArray(),
    var loadedLibs: Array<String> = emptyArray(),
    var loadedDexes: Array<String> = emptyArray()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NModPreloadData

        if (!assetsPacksPath.contentEquals(other.assetsPacksPath)) return false
        if (!loadedLibs.contentEquals(other.loadedLibs)) return false
        if (!loadedDexes.contentEquals(other.loadedDexes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = assetsPacksPath.contentHashCode()
        result = 31 * result + loadedLibs.contentHashCode()
        result = 31 * result + loadedDexes.contentHashCode()
        return result
    }
}
