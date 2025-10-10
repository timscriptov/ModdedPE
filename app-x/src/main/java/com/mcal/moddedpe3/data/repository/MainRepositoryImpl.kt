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
import com.mcal.pesdk.MinecraftInfo

class MainRepositoryImpl(
    private val context: Context
) : MainRepository {
    val minecraftInfo = MinecraftInfo(context)

    override fun getMinecraftPackageContext(): Context? {
        return minecraftInfo.getMinecraftPackageContext()
    }

    override fun getMinecraftPackageNativeLibraryDir(): String? {
        return minecraftInfo.getMinecraftPackageNativeLibraryDir()
    }

    override fun findMinecraftPackage(): PackageInfo? {
        return minecraftInfo.findMinecraftPackage()
    }

    override fun getMinecraftLabel(packageInfo: PackageInfo?): String {
        return minecraftInfo.getMinecraftLabel(packageInfo)
    }

    override fun getMinecraftVersionCode(packageInfo: PackageInfo?): Long {
        return minecraftInfo.getMinecraftVersionCode(packageInfo)
    }

    override fun getMinecraftVersionName(packageInfo: PackageInfo?): String {
        return minecraftInfo.getMinecraftVersionName(packageInfo)
    }

    override fun getMinecraftAssetPaths(): List<String> {
        return minecraftInfo.getMinecraftAssetPaths()
    }

    override fun getMinecraftPackageResourcePath(): String? {
        return minecraftInfo.getMinecraftPackageResourcePath()
    }

    override fun addAssetOverrides(assetManager: AssetManager) {
        minecraftInfo.addAssetOverrides(assetManager)
    }

    override fun addAssetPath(assetManager: AssetManager, path: String) {
        minecraftInfo.addAssetPath(assetManager, path)
    }

    override fun getDeviceABI(): String {
        return minecraftInfo.getDeviceABI()
    }

    override fun getMinecraftABI(): String {
        return minecraftInfo.getMinecraftABI()
    }

    override fun loadNativeLibrary(libraryName: String): Boolean {
        return minecraftInfo.loadNativeLibrary(libraryName)
    }
}
