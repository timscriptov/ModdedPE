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

import android.os.Parcelable
import com.mcal.pesdk3.nmod.NMod
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocalNMod(
    val packageName: String,
    val name: String,
    val versionName: String,
    val versionCode: Int,
    val author: String,
    val description: String,
    val isEnabled: Boolean,
    val isBugPack: Boolean,
    val warnings: List<String> = emptyList(),
    val changeLog: String? = null,
    val iconPath: String? = null,
    val bannerPath: String? = null,
) : Parcelable {
    companion object {
        fun fromNMod(nmod: NMod, isEnabled: Boolean = false): LocalNMod {
            return LocalNMod(
                packageName = nmod.getPackageName(),
                name = nmod.getName(),
                versionName = nmod.getVersionName(),
                versionCode = nmod.getVersionCode(),
                author = nmod.getAuthor(),
                description = nmod.getDescription(),
                isEnabled = isEnabled,
                isBugPack = nmod.isBugPack(),
                warnings = nmod.getWarnings().map { it.toString() },
                changeLog = nmod.getChangeLog(),
                iconPath = nmod.copyIconToData()?.path,
                bannerPath = nmod.copyBannerToData()?.path,
            )
        }
    }

    fun getDisplayName(): String {
        return name.ifBlank { packageName }
    }

    fun getVersionInfo(): String {
        return "v$versionName"
    }

    fun hasWarnings(): Boolean {
        return warnings.isNotEmpty()
    }
}
