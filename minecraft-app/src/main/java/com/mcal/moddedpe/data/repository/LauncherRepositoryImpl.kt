package com.mcal.moddedpe.data.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager
import com.mcal.moddedpe.GameActivity
import com.mcal.moddedpe.data.model.domain.AdConfigModel
import com.mcal.moddedpe.data.model.mapper.toDomain
import com.mcal.moddedpe.data.model.remote.AdConfigModelNT
import com.mcal.moddedpe.data.model.remote.ServersModelNT
import com.mcal.moddedpe.utils.FileHelper
import com.mcal.moddedpe.utils.ZipHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class LauncherRepositoryImpl(
    private val context: Context,
//    private val httpClient: HttpClient,
) : LauncherRepository {
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val mutex = Mutex()
    private val _item: MutableStateFlow<AdConfigModel?> = MutableStateFlow(null)

    override fun getDataFlow(): Flow<AdConfigModel> = flow {
        if (_item.value == null) {
            runCatching {
                getData()
            }
        }
        emitAll(_item.filterNotNull())
    }

    override suspend fun getData(): AdConfigModel = mutex.withLock {
        _item.value ?: updateData()
    }

    override suspend fun updateData(): AdConfigModel {
        val items = request()
        _item.update { items }
        return items
    }

    private suspend fun request(): AdConfigModel {
//        val response = httpClient.get("users")
//        val item = Json.decodeFromString<AdConfigModelNT>(response.bodyAsText())
        val jsonText = context.assets.open("resources/config.json").bufferedReader().use { it.readText() }
        return Json.decodeFromString<AdConfigModelNT>(jsonText).toDomain()
    }

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
            context.assets.open(asset).use { FileHelper.writeToFile(tempFile, it) }
            ZipHelper.extractFiles(tempFile, outputDir)
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
        val tempFile = File.createTempFile("temp", ".zip")

        val sourcePaths =
            context.applicationInfo.splitPublicSourceDirs?.toList() ?: listOf(context.applicationInfo.sourceDir)
        sourcePaths.forEach { path ->
            ZipFile(path).getInputStream(
                ZipEntry("lib/$abi/libgame.so")
            )?.copyTo(FileOutputStream(tempFile))
        }
        listOf(
            "libc++_shared.so",
            "libminecraftpe.so",
            "libMediaDecoders_Android.so"
        ).forEach { libName ->
            val libFile = File(nativeDir, libName)
            ZipFile(tempFile).getInputStream(
                ZipEntry(libName)
            )?.copyTo(FileOutputStream(libFile))
            libFile.setExecutable(true)
            libFile.setReadable(true)
            libFile.setWritable(true)
        }
        tempFile.delete()
        sharedPreferences.edit().putBoolean(PREF_INSTALLED_NATIVES, true).apply()
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

    companion object {
        private const val PREF_INSTALLED_SERVERS = "pref_installed_servers"
        private const val PREF_INSTALLED_RESOURCES = "pref_installed_resources"
        private const val PREF_INSTALLED_NATIVES = "pref_installed_natives"
    }
}
