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
package  com.mcal.editor.data.repository

import java.io.File

class TextEditorRepositoryImpl : TextEditorRepository {
    override suspend fun readFileContent(file: File): String {
        return file.readText()
    }

    override suspend fun writeFileContent(file: File, content: String) {
        file.writeText(content)
    }

    override fun calculateMaxLineLength(lines: List<String>): Int {
        return lines.maxOfOrNull { it.length } ?: 0
    }
}