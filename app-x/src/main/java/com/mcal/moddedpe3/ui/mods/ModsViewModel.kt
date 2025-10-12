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
package com.mcal.moddedpe3.ui.mods

import android.content.Context
import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mcal.moddedpe3.data.model.ImportResult
import com.mcal.moddedpe3.data.model.ImportState
import com.mcal.moddedpe3.data.model.ModsScreenState
import com.mcal.pesdk3.data.LocalNMod
import com.mcal.pesdk3.nmod.NModAPI
import com.mcal.pesdk3.nmod.toLocalNMod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ModsViewModel(
    private val context: Context,
) : ScreenModel {
    private val _state = MutableStateFlow(ModsScreenState())
    val state: StateFlow<ModsScreenState> = _state.asStateFlow()

    private var nModAPI: NModAPI = NModAPI(context)

    init {
        loadMods()
    }

    fun loadMods() {
        screenModelScope.launch {
            try {
                val enabledMods = nModAPI.getImportedEnabledNMods()
                val disabledMods = nModAPI.getImportedDisabledNMods()

                _state.update { currentState ->
                    currentState.copy(
                        allMods = nModAPI.getLoadedNMods(),
                        enabledMods = enabledMods.map { it.toLocalNMod(true) },
                        disabledMods = disabledMods.map { it.toLocalNMod(false) },
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        importState = ImportState.Error("Failed to load mods: ${e.message}")
                    )
                }
            }
        }
    }

    fun importModFromUri(uri: Uri?) {
        if (uri == null) {
            _state.update { currentState ->
                currentState.copy(
                    importState = ImportState.Error("No file selected")
                )
            }
            return
        }

        screenModelScope.launch {
            _state.update { currentState ->
                currentState.copy(
                    importState = ImportState.Loading
                )
            }

            try {
                when (val result = copyModFile(uri)) {
                    is ImportResult.Success -> {
                        _state.update { currentState ->
                            currentState.copy(
                                importState = ImportState.Success
                            )
                        }
                    }

                    is ImportResult.Error -> {
                        _state.update { currentState ->
                            currentState.copy(
                                importState = ImportState.Error(result.message)
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        importState = ImportState.Error("Import failed: ${e.message}")
                    )
                }
            }
            loadMods()
        }
    }

    private fun copyModFile(uri: Uri): ImportResult {
        return try {
            val inputStream =
                context.contentResolver.openInputStream(uri) ?: return ImportResult.Error("Cannot open file stream")

            val modsDir = File(context.filesDir, "mods").apply {
                if (!exists()) {
                    mkdirs()
                }
            }

            val fileName = "imported_mod_${System.currentTimeMillis()}.nmod"
            val outputFile = File(modsDir, fileName)
            if (outputFile.exists()) {
                return ImportResult.Error("A mod with the same name already exists")
            }

            inputStream.use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            val zippedNMod = try {
                nModAPI.archiveZippedNMod(outputFile.absolutePath)
            } catch (e: Exception) {
                outputFile.delete()
                return ImportResult.Error("Invalid mod file: ${e.message}")
            }

            try {
                nModAPI.importNMod(zippedNMod)
                outputFile.delete()
                return ImportResult.Success
            } catch (e: Exception) {
                outputFile.delete()
                return ImportResult.Error("Failed to import mod: ${e.message}")
            }
        } catch (e: SecurityException) {
            ImportResult.Error("Permission denied: ${e.message}")
        } catch (e: IOException) {
            ImportResult.Error("File operation failed: ${e.message}")
        } catch (e: Exception) {
            ImportResult.Error("Unexpected error: ${e.message}")
        }
    }

    fun resetImportState() {
        _state.update { currentState ->
            currentState.copy(
                importState = ImportState.Idle
            )
        }
    }

    fun toggleMod(localMod: LocalNMod, enabled: Boolean) {
        screenModelScope.launch {
            try {
                val allMods = nModAPI.getImportedEnabledNMods() + nModAPI.getImportedDisabledNMods()
                allMods.find {
                    it.getPackageName() == localMod.packageName
                }?.let {
                    nModAPI.setEnabled(it, enabled)
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        importState = ImportState.Error("Failed to toggle mod: ${e.message}")
                    )
                }
            }
            loadMods()
        }
    }

    fun deleteMod(mod: LocalNMod) {
        screenModelScope.launch {
            try {
                _state.value.allMods.find { it.getPackageName() == mod.packageName }?.let { nMod ->
                    nModAPI.removeImportedNMod(nMod)
                }
                _state.update { currentState ->
                    currentState.copy(
                        importState = ImportState.DeleteSuccess
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        importState = ImportState.Error("Failed to delete mod: ${e.message}")
                    )
                }
            }
            loadMods()
        }
    }

    fun moveModUp(mod: LocalNMod) {
        screenModelScope.launch {
            try {
                _state.value.allMods.find { it.getPackageName() == mod.packageName }?.let { nMod ->
                    nModAPI.upPosNMod(nMod)
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        importState = ImportState.Error("Failed to move mod up: ${e.message}")
                    )
                }
            }
            loadMods()
        }
    }

    fun moveModDown(mod: LocalNMod) {
        screenModelScope.launch {
            try {
                _state.value.allMods.find { it.getPackageName() == mod.packageName }?.let { nMod ->
                    nModAPI.downPosNMod(nMod)
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        importState = ImportState.Error("Failed to move mod down: ${e.message}")
                    )
                }
            }
            loadMods()
        }
    }

    fun canMoveUp(mod: LocalNMod): Boolean {
        val enabledMods = _state.value.enabledMods
        return enabledMods.indexOf(mod) > 0
    }

    fun canMoveDown(mod: LocalNMod): Boolean {
        val enabledMods = _state.value.enabledMods
        val index = enabledMods.indexOf(mod)
        return index >= 0 && index < enabledMods.size - 1
    }
}
