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
package com.mcal.worlds.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mcal.worlds.data.model.MinecraftWorld
import com.mcal.worlds.data.model.WorldsManagerScreenState
import com.mcal.worlds.data.repository.WorldRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class WorldsManagerViewModel(
    private val context: Context,
    private val worldRepository: WorldRepository
) : ScreenModel {
    private val _state = MutableStateFlow(
        WorldsManagerScreenState()
    )
    val state = _state.asStateFlow()

    init {
        loadWorlds()
    }

    fun loadWorlds() {
        screenModelScope.launch {
            val worlds = worldRepository.getWorlds()
            _state.update { currentState ->
                currentState.copy(
                    worlds = worlds,
                )
            }
        }
    }

    fun deleteWorld(worldDir: String) {
        screenModelScope.launch {
            val success = worldRepository.deleteWorld(worldDir)
            if (success) {
                loadWorlds()
            }
        }
    }

    fun shareWorld(world: MinecraftWorld) {
        screenModelScope.launch {
            val file = worldRepository.createWorldArchive(world)
            if (file != null) {
                shareWorldFile(file, world)
            }
        }
    }

    private fun shareWorldFile(file: File, world: MinecraftWorld) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = getMimeType(file)
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Minecraft World: ${world.name}")
                putExtra(Intent.EXTRA_TEXT, "Check out my Minecraft world: ${world.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val shareIntent = Intent.createChooser(intent, "Share Minecraft World").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            // Можно показать Toast с ошибкой
            android.widget.Toast.makeText(context, "Error sharing world", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeType(file: File): String {
        return when {
            file.extension.equals("mcworld", ignoreCase = true) -> "application/zip"
            else -> "*/*"
        }
    }
}
