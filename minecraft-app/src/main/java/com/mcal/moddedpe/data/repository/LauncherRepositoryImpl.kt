package com.mcal.moddedpe.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
import com.mcal.apkparser.zip.ZipFile
import com.mcal.moddedpe.GameActivity
import com.mcal.moddedpe.data.model.remote.ServersModelNT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

class LauncherRepositoryImpl(
    private val context: Context,
) : LauncherRepository {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private fun getServersList(): List<String> {
        val jsonText = context.assets.open("resources/servers.json").bufferedReader().use { it.readText() }
        return Json.decodeFromString<ServersModelNT>(jsonText).servers
    }

    override suspend fun installServers() = withContext(Dispatchers.IO) {
        val minecraftDir = File("${context.filesDir.parentFile}/games/com.mojang/minecraftpe")
        if (!minecraftDir.exists()) minecraftDir.mkdirs()
        File(minecraftDir, "external_servers.txt").bufferedWriter().use { writer ->
            getServersList().forEachIndexed { index, value ->
                writer.write("${index + 1}$value\n")
            }
        }
        sharedPreferences.edit().putBoolean(PREF_INSTALLED_SERVERS, true).apply()
    }

    override fun isInstalledServers(): Boolean {
        return sharedPreferences.getBoolean(PREF_INSTALLED_SERVERS, false)
    }

    override suspend fun installResources() = withContext(Dispatchers.IO) {
        val dataDir = context.filesDir.parentFile
        listOf(
            "resources/worlds.zip" to "games/com.mojang/minecraftWorlds",
            "resources/behavior_packs.zip" to "games/com.mojang/behavior_packs",
            "resources/resource_packs.zip" to "games/com.mojang/resource_packs",
            "resources/skin_packs.zip" to "games/com.mojang/skin_packs"
        ).forEach { (asset, output) ->
            val tempFile = File.createTempFile("temp", ".zip")
            val outputDir = File(dataDir, output)
            context.assets.open(asset).copyTo(FileOutputStream(tempFile))
            extractFiles(tempFile, outputDir)
            tempFile.delete()
        }
        sharedPreferences.edit().putBoolean(PREF_INSTALLED_RESOURCES, true).apply()
    }

    override fun isInstalledResources(): Boolean {
        return sharedPreferences.getBoolean(PREF_INSTALLED_RESOURCES, false)
    }

    override suspend fun installNatives() = withContext(Dispatchers.IO) {
        val abi = when {
            Build.SUPPORTED_64_BIT_ABIS.contains("arm64-v8a") -> "arm64-v8a"
            Build.SUPPORTED_32_BIT_ABIS.contains("armeabi-v7a") -> "armeabi-v7a"
            else -> {
                @Suppress("DEPRECATION")
                if (Build.CPU_ABI == "arm64-v8a") "arm64-v8a" else "armeabi-v7-a"
            }
        }

        val nativeDir = File(context.filesDir, "native").apply { if (!exists()) mkdirs() }
        val tempLibSoFile = File.createTempFile("temp", ".zip")

        var sourcePaths = context.applicationInfo.splitPublicSourceDirs?.toList()
        if (sourcePaths.isNullOrEmpty()) {
            sourcePaths = listOf(context.applicationInfo.sourceDir)
        }
        for (apkPath in sourcePaths) {
            val apkFile = File(apkPath)
            val name = apkFile.name
            if (name.contains("arm") || name == "base.apk") {
                runCatching {
                    getZipEntry(apkFile, "lib/$abi/libgame.so").copyTo(FileOutputStream(tempLibSoFile))
                }.onFailure {
                    Log.e(TAG, "Not found libgame.so: $abi $apkPath")
                }
            } else {
                Log.e(TAG, "Unknown APK file: $apkPath")
            }
        }
        listOf(
            "libc++_shared.so",
            "libminecraftpe.so",
            "libMediaDecoders_Android.so"
        ).forEach { libName ->
            val libFile = File(nativeDir, libName)
            getZipEntry(tempLibSoFile, libName).copyTo(FileOutputStream(libFile))
            libFile.setExecutable(true)
            libFile.setReadable(true)
            libFile.setWritable(true)
        }
        tempLibSoFile.delete()
        sharedPreferences.edit().putBoolean(PREF_INSTALLED_NATIVES, true).apply()
    }

    private fun getZipEntry(input: File, entry: String): ByteArrayInputStream {
        ZipFile(input).use { zipFile ->
            val zipEntry = zipFile.getEntry(entry)
            zipFile.getInputStream(zipEntry).use { inputStream ->
                return inputStream.readBytes().inputStream()
            }
        }
    }

    private fun extractFiles(input: File, output: File) {
        ZipFile(input).use { zipFile ->
            val enumeration = zipFile.entries
            while (enumeration.hasMoreElements()) {
                val ze = enumeration.nextElement()
                val file = File(output, ze.name)
                if (ze.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile?.mkdirs()
                    zipFile.getInputStream(ze).use { inputStream ->
                        FileOutputStream(file).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                }
            }
        }
    }

    override fun isInstalledNatives(): Boolean {
        return sharedPreferences.getBoolean(PREF_INSTALLED_NATIVES, false)
    }

    override fun startGame(activity: Activity) {
        val intent = Intent(activity, GameActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME)
        activity.startActivity(intent)
        activity.finish()
    }

    override fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                return activeNetworkInfo != null && activeNetworkInfo.isConnected
            }
        }
        return false
    }

    companion object {
        private val TAG = LauncherRepositoryImpl::class.java.name

        private const val PREF_INSTALLED_SERVERS = "pref_installed_servers"
        private const val PREF_INSTALLED_RESOURCES = "pref_installed_resources"
        private const val PREF_INSTALLED_NATIVES = "pref_installed_natives"
    }
}
