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
package com.mcal.moddedpe3.ui.editor

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mcal.moddedpe3.data.model.TextEditorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class TextEditorViewModel : ScreenModel {
    private val _state = MutableStateFlow(TextEditorState())
    val state = _state.asStateFlow()

    fun loadFileContent(file: File) {
        _state.value = _state.value.copy(isLoading = true)

        screenModelScope.launch {
            try {
                val content = withContext(Dispatchers.IO) {
                    file.readText()
                }
                _state.value = _state.value.copy(
                    content = content,
                    originalContent = content,
                    isLoading = false,
                    isModified = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    content = "Error reading file: ${e.message}",
                    originalContent = "",
                    isLoading = false,
                    error = "Failed to load file: ${e.message}"
                )
            }
        }
    }

    fun updateContent(newContent: String) {
        val currentState = _state.value
        _state.value = currentState.copy(
            content = newContent,
            isModified = newContent != currentState.originalContent
        )
    }

    fun saveFile(file: File) {
        _state.value = _state.value.copy(isLoading = true)

        screenModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    file.writeText(_state.value.content)
                }
                _state.value = _state.value.copy(
                    originalContent = _state.value.content,
                    isLoading = false,
                    isModified = false,
                    isSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to save file: ${e.message}"
                )
            }
        }
    }

    fun showSaveDialog() {
        _state.value = _state.value.copy(showSaveDialog = true)
    }

    fun hideSaveDialog() {
        _state.value = _state.value.copy(showSaveDialog = false)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun resetState() {
        _state.value = TextEditorState()
    }
}
