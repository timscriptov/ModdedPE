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
package com.mcal.files.data

import android.os.Parcelable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.TextSnippet
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class FileItem(val file: File) : Parcelable {
    val name: String get() = file.name
    val isDirectory: Boolean get() = file.isDirectory
    val size: String get() = if (file.isDirectory) "" else formatFileSize(file.length())
    val icon: @Composable () -> Unit = {
        Icon(
            imageVector = when {
                file.isDirectory -> Icons.Rounded.Folder
                isTextFile() -> Icons.Rounded.TextSnippet
                else -> Icons.Rounded.InsertDriveFile
            },
            contentDescription = null,
            tint = when {
                file.isDirectory -> Color(0xFFFFA000)
                isTextFile() -> Color(0xFF2196F3)
                else -> Color(0xFF757575)
            }
        )
    }

    fun isTextFile(): Boolean {
        val textExtensions = listOf("txt", "xml", "json", "properties", "cfg", "ini", "log")
        val name = file.name.lowercase()
        return textExtensions.any { name.endsWith(".$it") } ||
                file.extension.lowercase() in textExtensions
    }

    private fun formatFileSize(size: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB")
        var fileSize = size.toDouble()
        var unitIndex = 0

        while (fileSize > 1024 && unitIndex < units.size - 1) {
            fileSize /= 1024
            unitIndex++
        }

        return "%.1f %s".format(fileSize, units[unitIndex])
    }
}
