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
import android.content.pm.PackageInfo
import android.content.res.AssetManager

interface MainRepository {
    fun getMinecraftAssetPaths(): List<String>
    fun getMinecraftPackageResourcePath(): String?
    fun addAssetOverrides(assetManager: AssetManager)
    fun loadNativeLibrary(libraryName: String): Boolean
    fun getMinecraftPackageContext(): Context?
    fun getMinecraftPackageNativeLibraryDir(): String?
    fun isMinecraftAppBundle(): Boolean
    fun findMinecraftPackage(): PackageInfo?
    fun getMinecraftLabel(packageInfo: PackageInfo?): String
    fun getMinecraftVersionCode(packageInfo: PackageInfo?): Long
    fun getMinecraftVersionName(packageInfo: PackageInfo?): String
    fun getDeviceABI(): String
    fun getMinecraftABI(): String
}
