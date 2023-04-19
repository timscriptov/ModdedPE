package com.mcal.core.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object ABIHelper {
    fun getABI(): String {
        for (androidArch in Build.SUPPORTED_64_BIT_ABIS) {
            if (androidArch.contains("arm64-v8a")) {
                return "arm64-v8a"
            }
        }
        for (androidArch in Build.SUPPORTED_32_BIT_ABIS) {
            if (androidArch.contains("armeabi-v7a")) {
                return "armeabi-v7a"
            } else if (androidArch.contains("x86")) {
                return "x86"
            }
        }
        return Build.CPU_ABI
    }

    /**
     * Определяет архитектуру приложения из app.apk/lib/ARCH
     *
     * @param context
     * @return
     */
    fun getNameAppABI(context: Context): String {
        val deviceABI = getABI()
        var abi64: String? = null
        var abi32: String? = null
        try {
            val abis = getABIsFromApk(getPackageInfo(context).applicationInfo.sourceDir)
            if (abis != null) {
                for (a in abis) {
                    if (a.contains("arm64-v8a")) {
                        if (a.contains(deviceABI)) {
                            abi64 = "arm64-v8a"
                        } else {
                            abi32 = "armeabi-v7a"
                        }
                    } else if (a.contains("armeabi-v7a")) {
                        abi32 = "armeabi-v7a"
                    } else if (a.contains("x86")) {
                        abi32 = "x86"
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            println(e.message)
        }
        return abi64 ?: (abi32 ?: deviceABI)
    }

    @Throws(PackageManager.NameNotFoundException::class)
    fun getPackageInfo(context: Context): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
    }

    /**
     * Возвращает массив архитектур из app.apk/lib/ARCH
     *
     * @param apk
     * @return
     */
    fun getABIsFromApk(apk: String): Set<String>? {
        try {
            ZipFile(apk).use { apkFile ->
                val entries: Enumeration<out ZipEntry?> = apkFile.entries()
                val supportedABIs: MutableSet<String> = HashSet()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    if (entry != null) {
                        val name: String = entry.name
                        if (name.contains("../")) {
                            continue
                        }
                        if (name.startsWith("lib/") && !entry.isDirectory && name.endsWith(".so")) {
                            val supportedAbi =
                                name.substring(name.indexOf("/") + 1, name.lastIndexOf("/"))
                            supportedABIs.add(supportedAbi)
                        }
                    }
                }
                return supportedABIs
            }
        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }
}