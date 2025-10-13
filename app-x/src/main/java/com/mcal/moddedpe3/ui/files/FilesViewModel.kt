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
package com.mcal.moddedpe3.ui.files

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mcal.moddedpe3.data.model.FileItem
import com.mcal.moddedpe3.data.model.FilesScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class FilesViewModel : ScreenModel {
    private val _state = MutableStateFlow(FilesScreenState())
    val state = _state.asStateFlow()

    fun loadFiles(path: String) {
        _state.value = _state.value.copy(isLoading = true)

        screenModelScope.launch {
            val directory = File(path)
            val files = directory.listFiles() ?: emptyArray()

            val sortedFiles = files.sortedWith(
                compareBy(
                    { !it.isDirectory },
                    { it.name.lowercase() }
                )).map { FileItem(it) }

            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(
                    currentPath = path,
                    files = sortedFiles,
                    isLoading = false
                )
            }
        }
    }

    fun navigateToParent(currentPath: String, rootPath: String) {
        val parent = File(currentPath).parent ?: rootPath
        if (parent != currentPath) {
            loadFiles(parent)
        }
    }

    fun showDeleteDialog(file: FileItem) {
        _state.value = _state.value.copy(
            showDeleteDialog = true,
            fileToDelete = file
        )
    }

    fun hideDeleteDialog() {
        _state.value = _state.value.copy(
            showDeleteDialog = false,
            fileToDelete = null
        )
    }

    fun deleteFile(file: FileItem) {
        screenModelScope.launch {
            file.file.delete()
            loadFiles(_state.value.currentPath)
            hideDeleteDialog()
        }
    }

    fun showTextDialog(content: String) {
        _state.value = _state.value.copy(
            showTextDialog = true,
            textContent = content
        )
    }

    fun hideTextDialog() {
        _state.value = _state.value.copy(
            showTextDialog = false,
            textContent = ""
        )
    }

    fun readFileContent(file: File) {
        screenModelScope.launch {
            val content = withContext(Dispatchers.IO) {
                try {
                    file.readText()
                } catch (e: Exception) {
                    "Error reading file: ${e.message}"
                }
            }
            withContext(Dispatchers.Main) {
                showTextDialog(content)
            }
        }
    }
}
