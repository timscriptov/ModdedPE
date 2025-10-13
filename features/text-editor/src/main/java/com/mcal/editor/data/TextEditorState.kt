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
package com.mcal.editor.data

data class TextEditorState(
    val content: String = "",
    val originalContent: String = "",
    val isLoading: Boolean = false,
    val isModified: Boolean = false,
    val isSaved: Boolean = false,
    val showSaveDialog: Boolean = false,
    val error: String? = null,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)
