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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NModPreloadBean(
    @SerialName("native_libs")
    var nativeLibs: Array<NModLibInfo>? = null,
    @SerialName("assets_path")
    var assetsPath: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NModPreloadBean

        if (!nativeLibs.contentEquals(other.nativeLibs)) return false
        if (assetsPath != other.assetsPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nativeLibs?.contentHashCode() ?: 0
        result = 31 * result + (assetsPath?.hashCode() ?: 0)
        return result
    }
}