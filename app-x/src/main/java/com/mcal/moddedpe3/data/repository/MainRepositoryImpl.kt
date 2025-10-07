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
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Method
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class MainRepositoryImpl(
    private val context: Context
) : MainRepository {
    companion object {
        private const val PACKAGE_NAME = "com.mojang.minecraftpe"
        private val MINECRAFT_LIBS = listOf(
            "libc++_shared.so",
            "libfmod.so",
            "libmaesdk.so",
            "libMediaDecoders_Android.so",
            "libminecraftpe.so",
        )
        private val ASSET_SPLIT_PATHS = listOf(
            "split_install_pack.apk",
            "split_config.arm64_v8a.apk",
            "split_config.armeabi_v7a.apk",
            "split_config.x86_64.apk",
            "split_config.x86.apk",
            "split_config.en.apk",
            "split_config.assets.apk"
        )
    }

    override fun getMinecraftPackageContext(): Context? {
        return try {
            context.createPackageContext(
                PACKAGE_NAME,
                Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
            )
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("ModdedPE", "Minecraft not found")
            null
        }
    }

    private fun getNativeLibraryDir(): String {
        return context.cacheDir.path + "/lib/" + getMinecraftABI()
    }

    override fun getMinecraftPackageNativeLibraryDir(): String? {
        return if (isMinecraftAppBundle()) {
            copyNativeLibrariesFromAppBundle()
            getNativeLibraryDir()
        } else {
            getMinecraftPackageContext()?.applicationInfo?.nativeLibraryDir
        }
    }

    override fun isMinecraftAppBundle(): Boolean {
        return !getMinecraftPackageContext()?.applicationInfo?.splitPublicSourceDirs.isNullOrEmpty()
    }

    override fun findMinecraftPackage(): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(PACKAGE_NAME, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("ModdedPE", "Minecraft not found")
            null
        }
    }

    override fun getMinecraftLabel(packageInfo: PackageInfo?): String {
        return packageInfo?.applicationInfo?.loadLabel(context.packageManager)?.toString() ?: "unknown"
    }

    override fun getMinecraftVersionCode(packageInfo: PackageInfo?): Long {
        if (packageInfo == null) return -1
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
    }

    override fun getMinecraftVersionName(packageInfo: PackageInfo?): String {
        return packageInfo?.versionName ?: return "unknown"
    }

    override fun getMinecraftAssetPaths(): List<String> {
        val assetPaths = mutableListOf<String>()

        val basePath = getMinecraftPackageResourcePath() ?: return emptyList()
        assetPaths.add(basePath)

        // Добавляем другие возможные split APK с ассетами
        ASSET_SPLIT_PATHS.forEach { splitName ->
            val splitPath = basePath.replace("base.apk", splitName)
            if (File(splitPath).exists()) {
                assetPaths.add(splitPath)
            }
        }

        // Добавляем все split директории из App Bundle
        getMinecraftPackageContext()?.applicationInfo?.splitPublicSourceDirs?.forEach { splitPath ->
            if (File(splitPath).exists() && !assetPaths.contains(splitPath)) {
                assetPaths.add(splitPath)
            }
        }

        Log.d("ModdedPE", "Found ${assetPaths.size} asset paths: $assetPaths")
        return assetPaths
    }

    override fun getMinecraftPackageResourcePath(): String? {
        return getMinecraftPackageContext()?.packageResourcePath
    }

    override fun addAssetOverrides(assetManager: AssetManager) {
        try {
            val assetPaths = getMinecraftAssetPaths()
            assetPaths.forEach { path ->
                addAssetPath(assetManager, path)
            }
            Log.d("ModdedPE", "Added ${assetPaths.size} asset overrides")
        } catch (e: Exception) {
            Log.e("ModdedPE", "Error adding asset overrides", e)
        }
    }

    private fun addAssetPath(assetManager: AssetManager, path: String) {
        try {
            val method: Method = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
            val result = method.invoke(assetManager, path) as Int
            if (result == 0) {
                Log.w("ModdedPE", "Failed to add asset path: $path")
            } else {
                Log.d("ModdedPE", "Successfully added asset path: $path (cookie: $result)")
            }
        } catch (e: Exception) {
            Log.e("ModdedPE", "Error adding asset path: $path", e)
        }
    }

    override fun getDeviceABI(): String {
        for (androidArch in Build.SUPPORTED_64_BIT_ABIS) {
            if (androidArch.contains("arm64-v8a")) {
                return "arm64-v8a"
            } else if (androidArch.contains("x86_64")) {
                return "x86_64"
            }
        }
        for (androidArch in Build.SUPPORTED_32_BIT_ABIS) {
            if (androidArch.contains("armeabi-v7a")) {
                return "armeabi-v7a"
            } else if (androidArch.contains("x86")) {
                return "x86"
            }
        }
        @Suppress("DEPRECATION")
        return Build.CPU_ABI
    }

    override fun getMinecraftABI(): String {
        return if (isMinecraftAppBundle()) {
            findABIFromSplitApks() ?: getDeviceABI()
        } else {
            getMinecraftPackageContext()?.applicationInfo?.nativeLibraryDir?.let { nativeLibDir ->
                when {
                    nativeLibDir.contains("arm64-v8a") -> "arm64-v8a"
                    nativeLibDir.contains("armeabi-v7a") -> "armeabi-v7a"
                    nativeLibDir.contains("x86_64") -> "x86_64"
                    nativeLibDir.contains("x86") -> "x86"
                    else -> getDeviceABI()
                }
            } ?: getDeviceABI()
        }
    }

    private fun copyNativeLibrariesFromAppBundle(): Boolean {
        return try {
            val mcContext = getMinecraftPackageContext() ?: return false
            val appInfo = mcContext.applicationInfo
            val targetABI = getMinecraftABI()
            val cacheLibDir = File(getNativeLibraryDir())

            if (!cacheLibDir.exists()) {
                cacheLibDir.mkdirs()
            }

            // Проверяем, нужно ли копировать библиотеки
            if (areLibrariesAlreadyCopied(cacheLibDir)) {
                Log.d("ModdedPE", "Libraries already copied to cache")
                return true
            }

            var success = false

            // Ищем библиотеки во всех split APK
            appInfo.splitPublicSourceDirs?.forEach { splitPath ->
                if (copyLibrariesFromSplit(splitPath, targetABI, cacheLibDir)) {
                    success = true
                }
            }

            // Если не нашли в splits, проверяем основной APK
            if (!success) {
                success = copyLibrariesFromSplit(appInfo.sourceDir, targetABI, cacheLibDir)
            }

            if (success) {
                Log.d("ModdedPE", "Successfully copied native libraries to cache")
            } else {
                Log.e("ModdedPE", "Failed to copy native libraries to cache")
            }

            success
        } catch (e: Exception) {
            Log.e("ModdedPE", "Error copying native libraries", e)
            false
        }
    }

    private fun copyLibrariesFromSplit(splitPath: String, targetABI: String, targetDir: File): Boolean {
        return try {
            val splitFile = File(splitPath)
            if (!splitFile.exists()) return false

            var copiedAny = false
            ZipFile(splitFile).use { zip ->
                val entries = zip.entries()

                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val entryPath = entry.name

                    // Ищем библиотеки для нужной ABI
                    if (entryPath.startsWith("lib/$targetABI/") && !entry.isDirectory) {
                        val libName = entryPath.substringAfterLast('/')
                        if (MINECRAFT_LIBS.contains(libName)) {
                            if (extractLibraryFromZip(zip, entry, File(targetDir, libName))) {
                                copiedAny = true
                                Log.d("ModdedPE", "Copied library: $libName")
                            }
                        }
                    }
                }
            }
            copiedAny
        } catch (e: Exception) {
            Log.e("ModdedPE", "Error copying libraries from split: $splitPath", e)
            false
        }
    }

    private fun extractLibraryFromZip(zip: ZipFile, entry: ZipEntry, outputFile: File): Boolean {
        return try {
            zip.getInputStream(entry).use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }
            // Устанавливаем права на выполнение
            outputFile.setReadable(true, false)
            outputFile.setExecutable(true, false)
            true
        } catch (e: Exception) {
            Log.e("ModdedPE", "Error extracting library: ${entry.name}", e)
            false
        }
    }

    private fun areLibrariesAlreadyCopied(cacheLibDir: File): Boolean {
        return MINECRAFT_LIBS.all { libName ->
            val libFile = File(cacheLibDir, libName)
            libFile.exists() && libFile.length() > 0
        }
    }

    override fun loadNativeLibraries(): Boolean {
        return try {
            val nativeLibDir = getMinecraftPackageNativeLibraryDir()
            if (nativeLibDir != null) {
                val libDir = File(nativeLibDir)
                if (libDir.exists() && libDir.isDirectory) {
                    var successCount = 0
                    MINECRAFT_LIBS.forEach { libName ->
                        val libFile = File(libDir, libName)
                        if (libFile.exists()) {
                            try {
                                System.load(libFile.absolutePath)
                                Log.d("ModdedPE", "Successfully loaded native library: $libName")
                                successCount++
                            } catch (e: UnsatisfiedLinkError) {
                                Log.e("ModdedPE", "Failed to load native library: $libName", e)
                            } catch (e: SecurityException) {
                                Log.e("ModdedPE", "Security exception loading native library: $libName", e)
                            }
                        } else {
                            Log.w("ModdedPE", "Native library not found: $libName at ${libFile.absolutePath}")
                        }
                    }

                    // Проверяем, что все основные библиотеки загружены
                    successCount >= MINECRAFT_LIBS.size - 1 // Допускаем одну неудачу
                } else {
                    Log.e("ModdedPE", "Native library directory not found or is not a directory: $nativeLibDir")
                    false
                }
            } else {
                Log.e("ModdedPE", "Failed to get Minecraft native library directory")
                false
            }
        } catch (e: Exception) {
            Log.e("ModdedPE", "Error loading native libraries", e)
            false
        }
    }

    private fun findABIFromSplitApks(): String? {
        return try {
            val mcContext = getMinecraftPackageContext() ?: return null
            val appInfo = mcContext.applicationInfo

            // Проверяем все split APK
            appInfo.splitPublicSourceDirs?.forEach { splitPath ->
                val abi = extractABIFromSplitApk(splitPath)
                if (abi != null) {
                    return abi
                }
            }

            // Если в splits не нашли, проверяем основной APK
            extractABIFromSplitApk(appInfo.sourceDir)
        } catch (e: Exception) {
            Log.e("ModdedPE", "Error finding ABI from split APKs", e)
            null
        }
    }

    private fun extractABIFromSplitApk(apkPath: String?): String? {
        if (apkPath.isNullOrEmpty()) return null

        return try {
            ZipFile(apkPath).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    // Ищем нативные библиотеки в структуре lib/abi/
                    if (entry.name.startsWith("lib/") && !entry.isDirectory) {
                        val pathParts = entry.name.split('/')
                        if (pathParts.size >= 3) { // lib/abi/library.so
                            val abi = pathParts[1]
                            if (isValidABI(abi)) {
                                return abi
                            }
                        }
                    }

                    // Дополнительно проверяем манифест split'а для config.abi splits
                    if (entry.name == "AndroidManifest.xml" || entry.name.contains("config.")) {
                        val fileName = File(apkPath).name
                        val detectedABI = detectABIFromFileName(fileName)
                        if (detectedABI != null) {
                            return detectedABI
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            Log.e("ModdedPE", "Error extracting ABI from APK: $apkPath", e)
            null
        }
    }

    private fun detectABIFromFileName(fileName: String): String? {
        return when {
            fileName.contains("arm64_v8a") || fileName.contains("arm64-v8a") -> "arm64-v8a"
            fileName.contains("armeabi_v7a") || fileName.contains("armeabi-v7a") -> "armeabi-v7a"
            fileName.contains("x86_64") -> "x86_64"
            fileName.contains("x86") && !fileName.contains("x86_64") -> "x86"
            else -> null
        }
    }

    private fun isValidABI(abi: String): Boolean {
        return abi in listOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
    }
}