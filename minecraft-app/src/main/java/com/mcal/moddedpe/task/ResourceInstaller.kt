package com.mcal.moddedpe.task

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.mcal.moddedpe.BuildConfig
import com.mcal.moddedpe.utils.ABIHelper
import com.mcal.moddedpe.utils.FileHelper
import com.mcal.moddedpe.utils.ZipHelper
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

object ResourceInstaller {
    private val serverList = listOf(
        ":§l§a#1 Сервер (RU):s46.minesrv.ru:19132:1568120063",
        ":§l§a#2 Сервер (RU):s47.minesrv.ru:19132:1568120044",
        ":§l§a#4 Сервер (RU):s48.minesrv.ru:19132:1568120032",
        ":§l§a#5 Craftersmc :play.craftersmc.net:19132",
        ":§l§a#1 Complex Gaming :mps.mc-complex.com:19132",
        ":§l§a#2 WASDCRAFT\t\t :WasdCraft.aternos.me:61291",
        ":§l§a#3 MCHub :pe.mchub.com:19132",
        ":§l§a# AkumaMC :bedrock.akumamc.net:19132",
        ":§l§a# NetherGames Network :play.nethergames.org:19132",
        ":§l§a# FadeCloud :mp.fadecloud.com:19132",
        ":§l§a# Havoc Games - The Mining Dead :pocket.havoc.games:19132",
        ":§l§a# HyperLands :play.hyperlandsmc.net:19132",
        ":§l§a# Vortex Network :bedrock.vortexnetwork.net:19132",
        ":§l§a# Plutonium :mcpe.plutonium.best:19132",
        ":§l§a# LemonCloud :mps.lemoncloud.net:19132",
        ":§l§a# JackpotMC :bedrock.jackpotmc.com:19132",
        ":§l§a# Rede Revo :jogar.rederevo.com:19132",
        ":§l§a# Twerion :bedrock.twerion.net:19132",
        ":§l§a# Zeqa Practice :zeqa.net:19132",
        ":§l§a# SaloonNetwork.com Türk Hub Sunucu! :play.saloonnetwork.com:19132",
        ":§l§a# Overy - Skyblock :play.overymc.fr:19132",
        ":§l§a# EmperialsPE // ESPE :play.emperials.net:19132",
        ":§l§a# MineWave :br.minewave.net:19132",
        ":§l§a# 7TogkSMP Revolution :play.7togkmc.id:19132",
        ":§l§a# KirizaNetwork :play.kirizanetwork.xyz:19132",
        ":§l§a# Türk MuzCraft MCPE.MUZCRAFT.COM\t :mcpe.muzcraft.com:19132",
        ":§l§a# RulerCraft\t :play.rulercraft.com:25565",
        ":§l§a# FazoreCraft\t :play.fazorecraft.xyz:19132",
        ":§l§a# Sunrise SMP\t :play.sunrisemc.xyz:19130"
    )

    fun install(context: Context) {
        if (!isInstalled(context)) {
            runCatching {
                extractResources(context)
            }.onSuccess {
                setIsInstalled(context, true)
            }
        }
        if (!isAddedServers(context)) {
            runCatching {
                addServers(context)
            }.onSuccess {
                setAddedServers(context, true)
            }
        }
        runCatching {
            updateSettings(context)
        }
        if (!isExtractedNatives(context)) {
            runCatching {
                extractNatives(context)
            }.onSuccess {
                setExtractedNatives(context, true)
            }.onFailure {
                restartApp(context)
            }
        }
    }

    private fun extractResources(context: Context) {
        val resources = listOf(
            "resources/worlds.zip" to "games/com.mojang/minecraftWorlds",
            "resources/behavior_packs.zip" to "games/com.mojang/behavior_packs",
            "resources/resource_packs.zip" to "games/com.mojang/resource_packs",
            "resources/skin_packs.zip" to "games/com.mojang/skin_packs"
        )
        resources.forEach { (asset, output) ->
            extract(context, asset, output)
        }
    }

    private fun addServers(context: Context) {
        val serverFile = getServerFile(context)
        if (!serverFile.exists()) {
            serverFile.createNewFile()
            writeServersToFile(serverFile)
        }
    }

    private fun extractNatives(context: Context) {
        val abi = getDeviceABI()
        extractLibArchive(context, abi)
        extractLibraries(context, abi)
    }

    private fun updateSettings(context: Context) {
        val minecraftPEDir = File(context.filesDir.parent, "games/com.mojang/minecraftpe/")
        val options = File(minecraftPEDir, "options.txt")
        val contentBuilder = StringBuilder()

        val defaultSettings = mapOf(
            "new_edit_world_screen_beta" to "0",
            "new_play_screen_beta" to "0",
            "has_shown_storage_location_warning" to "1",
            "touch_control_selection_screen" to "0",
            "ctrl_enableNewTouchControlSchemes" to "1",
            "has_shown_being_sunset_notice" to "1",
            "has_shown_sunset_notice" to "1",
            "do_not_show_cloud_upload_prompt" to "1",
            "game_hasshownpatchnotice" to "1",
            "day_one_experience_completed" to "1",
            "game_shownplatformpremiumupsell" to "1",
            "do_not_show_multiplayer_ip_safety_warning" to "1",
            "playfab_commerce_enabled" to "0",
            "realms_view_upsell_screen_count" to "7",
            "enable_braze" to "0",
            "shown_ratings_prompt" to "1",
            "game_haschosennottosignintoxbl" to "1",
            "control_tips_should_be_shown" to "0",
            "device_lost_telemetry_enabled" to "0",
            "game_haseverloggedintoxbl" to "1",
            "allow_cellular_data" to "1",
            "gfx_fancygraphics" to "0",
            "gfx_viewbobbing" to "0",
            "gfx_smoothlighting" to "0"
        )

        if (options.exists()) {
            for (line in options.bufferedReader().readLines()) {
                if (line.isNotEmpty()) {
                    contentBuilder.append(line).append("\n")
                    defaultSettings.forEach { (key, value) ->
                        if (line.contains(key)) {
                            contentBuilder.append("$key:$value\n")
                        }
                    }
                }
            }
        } else {
            defaultSettings.forEach { (key, value) ->
                contentBuilder.append("$key:$value\n")
            }
        }
        options.writeBytes(contentBuilder.toString().toByteArray(StandardCharsets.UTF_8))
        deleteCache(context)
    }

    private fun deleteCache(context: Context) {
        val filesDir = context.filesDir
        val treatmentsDir = File(context.filesDir.parent, "treatments")
        if (treatmentsDir.exists()) {
            treatmentsDir.deleteRecursively()
        }
        val cache = File(filesDir.parent, "cache")
        if (cache.exists()) {
            cache.deleteRecursively()
        }
        val codeCache = File(filesDir.parent, "code_cache")
        if (codeCache.exists()) {
            codeCache.deleteRecursively()
        }
        val cdn = File(filesDir.parent, "cdn")
        if (cdn.exists()) {
            cdn.deleteRecursively()
        }
        val crash = File(filesDir.parent, "crash")
        if (crash.exists()) {
            crash.deleteRecursively()
        }
        val premiumCache = File(filesDir.parent, "premium_cache")
        if (premiumCache.exists()) {
            premiumCache.deleteRecursively()
        }
    }

    private fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun getDeviceABI(): String {
        val deviceABI = ABIHelper.getABI()
        return if (deviceABI == "x86_64") "x86" else deviceABI
    }

    private fun extractLibArchive(context: Context, abi: String) {
        runCatching {
            val archive = File(getNativeDir(context), "libgame.zip")
            if (isAppBundle(context)) {
                extractFromAppBundle(context, abi, archive)
            } else {
                extractFromAPK(context, abi, archive)
            }
            archive.delete()
        }
    }

    private fun extractFromAppBundle(context: Context, abi: String, archive: File) {
        context.applicationInfo.splitPublicSourceDirs?.forEach { path ->
            val name = File(path).name
            if (name.contains("arm") || name.contains("x86")) {
                ZipFile(path).getInputStream(ZipEntry("lib/$abi/libgame.so"))?.use {
                    FileHelper.writeToFile(archive, it)
                }
            }
        }
    }

    private fun extractFromAPK(context: Context, abi: String, archive: File) {
        ZipFile(context.applicationInfo.sourceDir).getInputStream(
            ZipEntry("lib/$abi/libgame.so")
        )?.use {
            FileHelper.writeToFile(archive, it)
        }
    }

    private fun isAppBundle(context: Context): Boolean {
        return !context.applicationInfo.splitPublicSourceDirs.isNullOrEmpty()
    }


    @SuppressLint("UnsafeDynamicallyLoadedCode")
    private fun extractLibraries(context: Context, abi: String) {
        val archive = File(getNativeDir(context), "libgame.zip")
        val outDir = File(getNativeDir(context), abi)
        if (!outDir.exists()) {
            outDir.mkdirs()
        }
        listOf(
            "libc++_shared.so",
            "libminecraftpe.so",
            "libMediaDecoders_Android.so"
        ).forEach { libName ->
            extractLibrary(archive, outDir, libName)
        }
    }

    private fun extractLibrary(archive: File, outDir: File, libName: String) {
        val libFile = File(outDir, libName)
        ZipFile(archive).getInputStream(ZipEntry(libName))?.use {
            FileHelper.writeToFile(libFile, it)
        }
        libFile.apply {
            setExecutable(true)
            setReadable(true)
            setWritable(true)
        }
    }

    private fun isExtractedNatives(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("installed_native_" + BuildConfig.VERSION_CODE, false)
    }

    private fun setExtractedNatives(context: Context, mode: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean("installed_native_" + BuildConfig.VERSION_CODE, mode).apply()
    }

    private fun getNativeDir(context: Context): File {
        val dir = File(context.filesDir, "native")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun getServerFile(context: Context): File {
        val minecraftDir = File("${context.filesDir.parentFile}/games/com.mojang/minecraftpe")
        if (!minecraftDir.exists()) {
            minecraftDir.mkdirs()
        }
        return File(minecraftDir, "external_servers.txt")
    }

    private fun writeServersToFile(file: File) {
        file.bufferedWriter().use { writer ->
            serverList.forEachIndexed { index, value ->
                writer.write("${index + 1}$value\n")
            }
        }
    }

    private fun isAddedServers(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("added_custom_servers", false)
    }

    private fun setAddedServers(context: Context, mode: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean("added_custom_servers", mode).apply()
    }

    private fun extract(context: Context, assetsName: String, output: String) {
        try {
            val tmp = File.createTempFile("temp", ".zip")
            val outputDir = File(context.filesDir.parentFile, output)
            context.assets.open(assetsName).use {
                FileHelper.writeToFile(tmp, it)
            }
            ZipHelper.extractFiles(tmp, outputDir)
            tmp.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isInstalled(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getBoolean("installed_resources_" + BuildConfig.VERSION_CODE, false)
    }

    private fun setIsInstalled(context: Context, mode: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putBoolean("installed_resources_" + BuildConfig.VERSION_CODE, mode).apply()
    }
}
