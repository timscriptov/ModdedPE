package com.mcal.moddedpe.task

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import java.io.File
import java.io.IOException

class CustomServers(
    private val context: Context
) {
    fun install() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (sharedPreferences.getBoolean("added_custom_servers", true)) {
            val arraySet = mutableSetOf(
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
            try {
                val minecraftDir = File("${context.filesDir.parentFile}/games/com.mojang/minecraftpe")
                if (!minecraftDir.exists()) {
                    minecraftDir.mkdirs()
                }
                val serverFile = File(minecraftDir, "external_servers.txt")
                if (!serverFile.exists()) {
                    serverFile.createNewFile()
                    serverFile.bufferedWriter().use { writer ->
                        arraySet.forEachIndexed { index, value ->
                            writer.write("${index + 1}$value\n")
                        }
                    }
                }
                sharedPreferences.edit().putBoolean("added_custom_servers", false).apply()
            } catch (e: IOException) {
                Log.e("ModdedPE", "IOException while creating file: $e")
            }
        }
    }
}