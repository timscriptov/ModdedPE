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
package com.mcal.worlds.data.repository

import android.content.Context
import com.mcal.worlds.data.model.MinecraftWorld
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class WorldRepositoryImpl(
    private val context: Context
) : WorldRepository {
    private val worldsBasePath = File(context.filesDir.parentFile, "games/com.mojang/minecraftWorlds")

    override suspend fun getWorlds(): List<MinecraftWorld> = withContext(Dispatchers.IO) {
        val worlds = mutableListOf<MinecraftWorld>()

        if (!worldsBasePath.exists()) {
            return@withContext worlds
        }

        worldsBasePath.listFiles()?.forEach { worldDir ->
            if (worldDir.isDirectory) {
                val worldName = getWorldName(worldDir)
                val iconPath = getWorldIconPath(worldDir)
                val lastPlayed = getLastPlayed(worldDir)

                worlds.add(
                    MinecraftWorld(
                        worldDir = worldDir.name,
                        name = worldName,
                        iconPath = iconPath,
                        lastPlayed = lastPlayed
                    )
                )
            }
        }

        worlds.sortedByDescending { it.lastPlayed }
    }

    private fun getWorldName(worldDir: File): String {
        val levelNameFile = File(worldDir, "levelname.txt")
        return if (levelNameFile.exists()) {
            levelNameFile.readText().trim()
        } else {
            worldDir.name
        }
    }

    private fun getWorldIconPath(worldDir: File): String? {
        val iconFile = File(worldDir, "world_icon.jpeg")
        return if (iconFile.exists()) {
            iconFile.absolutePath
        } else {
            null
        }
    }

    private fun getLastPlayed(worldDir: File): Long {
        val levelDatFile = File(worldDir, "level.dat")
        return if (levelDatFile.exists()) {
            levelDatFile.lastModified()
        } else {
            worldDir.lastModified()
        }
    }

    override suspend fun deleteWorld(worldDir: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val worldFolder = File(worldsBasePath, worldDir)
            if (worldFolder.exists() && worldFolder.isDirectory) {
                worldFolder.deleteRecursively()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun createWorldArchive(world: MinecraftWorld): File? = withContext(Dispatchers.IO) {
        try {
            val worldFolder = File(worldsBasePath, world.worldDir)
            if (!worldFolder.exists() || !worldFolder.isDirectory) {
                return@withContext null
            }

            val tempDir = File(context.cacheDir, "worlds_share")
            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }

            val worldName = getWorldName(worldFolder)
            val archiveFile = File(tempDir, "$worldName.mcworld")

            FileOutputStream(archiveFile).use { fileStream ->
                ZipOutputStream(fileStream).use { zipStream ->
                    worldFolder.listFiles()?.forEach { file ->
                        if (file.isFile) {
                            addFileToZip(file, worldFolder, zipStream)
                        } else if (file.isDirectory) {
                            addDirectoryToZip(file, worldFolder, zipStream)
                        }
                    }
                }
            }

            archiveFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun addFileToZip(file: File, baseDir: File, zipStream: ZipOutputStream) {
        try {
            val entryName = file.relativeTo(baseDir).path
            val zipEntry = ZipEntry(entryName)
            zipStream.putNextEntry(zipEntry)

            file.inputStream().use { input ->
                input.copyTo(zipStream)
            }

            zipStream.closeEntry()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addDirectoryToZip(directory: File, baseDir: File, zipStream: ZipOutputStream) {
        directory.listFiles()?.forEach { file ->
            if (file.isFile) {
                addFileToZip(file, baseDir, zipStream)
            } else if (file.isDirectory) {
                addDirectoryToZip(file, baseDir, zipStream)
            }
        }
    }
}
