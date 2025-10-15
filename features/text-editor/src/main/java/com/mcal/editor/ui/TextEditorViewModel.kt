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
package com.mcal.editor.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mcal.editor.data.TextEditorState
import com.mcal.editor.data.repository.TextEditorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

class TextEditorViewModel(
    private val file: File,
    private val textEditorRepository: TextEditorRepository
) : ScreenModel {
    private val _state = MutableStateFlow(TextEditorState())
    val state = _state.asStateFlow()

    private val undoStack = LinkedList<String>()
    private val redoStack = LinkedList<String>()
    private var isUndoRedoOperation = false

    init {
        loadFileContent(file)
    }

    fun loadFileContent(file: File) {
        _state.value = _state.value.copy(isLoading = true)

        screenModelScope.launch {
            try {
                val content = textEditorRepository.readFileContent(file)
                undoStack.clear()
                redoStack.clear()
                undoStack.push(content)

                _state.value = _state.value.copy(
                    content = content,
                    originalContent = content,
                    isLoading = false,
                    isModified = false,
                    canUndo = false,
                    canRedo = false
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

        if (!isUndoRedoOperation) {
            undoStack.push(currentState.content)
            redoStack.clear()
        }

        _state.value = currentState.copy(
            content = newContent,
            isModified = newContent != currentState.originalContent,
            canUndo = undoStack.size > 1,
            canRedo = redoStack.isNotEmpty()
        )
    }

    fun undo() {
        if (undoStack.size > 1) {
            isUndoRedoOperation = true

            redoStack.push(_state.value.content)

            undoStack.pop()

            val previousContent = undoStack.peek()
            updateContent(previousContent ?: "")

            isUndoRedoOperation = false

            _state.value = _state.value.copy(
                canUndo = undoStack.size > 1,
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            isUndoRedoOperation = true

            undoStack.push(_state.value.content)

            val nextContent = redoStack.pop()
            updateContent(nextContent)

            isUndoRedoOperation = false

            _state.value = _state.value.copy(
                canUndo = undoStack.size > 1,
                canRedo = redoStack.isNotEmpty()
            )
        }
    }

    fun saveFile(file: File) {
        _state.value = _state.value.copy(isLoading = true)

        screenModelScope.launch {
            try {
                textEditorRepository.writeFileContent(file, _state.value.content)
                undoStack.clear()
                redoStack.clear()
                undoStack.push(_state.value.content)

                _state.value = _state.value.copy(
                    originalContent = _state.value.content,
                    isLoading = false,
                    isModified = false,
                    isSaved = true,
                    error = null,
                    canUndo = false,
                    canRedo = false
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
        undoStack.clear()
        redoStack.clear()
    }
}
